package austeretony.oxygen_store.server;

import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.notification.SimpleNotification;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.sound.OxygenSoundEffects;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.api.CurrencyHelperServer;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.PrivilegesProviderServer;
import austeretony.oxygen_core.server.api.SoundEventHelperServer;
import austeretony.oxygen_core.server.api.TimeHelperServer;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.main.EnumStorePrivilege;
import austeretony.oxygen_store.common.main.EnumStoreStatusMessage;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.common.network.client.CPPurchaseSuccessful;
import austeretony.oxygen_store.common.network.client.CPRemoveGift;
import austeretony.oxygen_store.common.store.EnumPurchaseType;
import austeretony.oxygen_store.common.store.OfferData;
import austeretony.oxygen_store.common.store.StoreOffer;
import austeretony.oxygen_store.common.store.gift.EnumGiftType;
import austeretony.oxygen_store.common.store.gift.Gift;
import net.minecraft.entity.player.EntityPlayerMP;

public class StoreOperationsManagerServer {

    private final StoreManagerServer manager;

    private final Queue<QueuedStorePurchase> purchasesQueue = new ConcurrentLinkedQueue<>();

    public StoreOperationsManagerServer(StoreManagerServer manager) {
        this.manager = manager;
    }

    public void processExpiredGifts() {
        final Runnable task = ()->{
            Iterator<Gift> iterator;
            Gift gift;
            int removed = 0;
            for (StorePlayerData playerData : this.manager.getPlayersDataContainer().getPlayersData()) {
                iterator = playerData.getGifts().iterator();
                while (iterator.hasNext()) {
                    gift = iterator.next();
                    if (gift != null
                            && gift.getType() == EnumGiftType.PENDING
                            && !gift.isSystemGift()
                            && gift.isExpired(TimeHelperServer.getCurrentMillis())) {
                        this.processExpiredGift(gift);
                        iterator.remove();
                        removed++;
                    }
                }
            }
            OxygenMain.LOGGER.info("[Store] Removed {} expired gifts.", removed);
        };
        OxygenHelperServer.addRoutineTask(task);
    }

    private void processExpiredGift(Gift gift) {
        StorePlayerData senderData = StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(gift.getSenderUUID());
        senderData.addGift(new Gift(
                senderData.createGiftId(TimeHelperServer.getCurrentMillis()),
                gift.getOfferPersistentId(),
                gift.getSenderUUID(),
                gift.getSenderUsername(),
                gift.getReceiverUsername(),
                gift.getMessage(),
                EnumGiftType.RETURNED));
        StoreManagerServer.instance().getPlayersDataContainer().setChanged(true);
    }

    void process() {
        final Runnable task = ()->{
            while (!this.purchasesQueue.isEmpty()) {
                final QueuedStorePurchase queued = this.purchasesQueue.poll();
                if (queued != null) {
                    switch (queued.type) {
                    case SELF:
                        purchaseSelf(queued.playerMP, queued.offerPersistentId);
                        break;
                    case GIFT:
                        purchaseGift(queued.playerMP, queued.offerPersistentId, queued.receiverUUID, queued.message);
                        break;
                    }
                }
            }
        };
        OxygenHelperServer.addRoutineTask(task);
    }

    public void purchase(EntityPlayerMP playerMP, EnumPurchaseType type, long offerPersistentId, 
            @Nullable UUID receiverUUID, @Nullable String message) {
        this.purchasesQueue.offer(new QueuedStorePurchase(playerMP, type, offerPersistentId, receiverUUID, message));
    }

    private void purchaseSelf(EntityPlayerMP playerMP, long offerPersistentId) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if ((StoreConfig.ENABLE_STORE_ACCESS_CLIENTSIDE.asBoolean() || OxygenHelperServer.checkTimeOut(playerUUID, StoreMain.STORE_TIMEOUT_ID))
                && PrivilegesProviderServer.getAsBoolean(playerUUID, EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean())) {
            StoreOffer offer = StoreManagerServer.instance().getOffersContainer().getOfferByPersistentId(offerPersistentId);
            if (offer != null
                    && offer.isAvailable()) {
                if (StoreConfig.ADVANCED_LOGGING.asBoolean())
                    OxygenMain.LOGGER.info("[Store] <{}/{}> [1]: trying to purchase {}({})...",
                            CommonReference.getName(playerMP), 
                            playerUUID,
                            offer.getName(),
                            offerPersistentId);

                StorePlayerData playerData = StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(playerUUID);
                if (playerData == null)
                    playerData = StoreManagerServer.instance().getPlayersDataContainer().createPlayerData(playerUUID);
                OfferData offerData = playerData.getOfferDataByOfferPersistentId(offerPersistentId);
                if (offerData == null) {
                    offerData = new OfferData(
                            playerData.createOfferDataId(TimeHelperServer.getCurrentMillis()), 
                            offerPersistentId);
                    playerData.addOfferData(offerData);
                }

                if (offerData.canPurchase(offer, TimeHelperServer.getCurrentMillis())
                        && (offer.isFree() || CurrencyHelperServer.enoughCurrency(playerUUID, offer.isSale() ? offer.getSalePrice() : offer.getPrice(), StoreConfig.STORE_CURRENCY_INDEX.asInt()))
                        && offer.getGoods().collect(playerMP)) {
                    if (!offer.isFree())
                        CurrencyHelperServer.removeCurrency(playerUUID, offer.isSale() ? offer.getSalePrice() : offer.getPrice(), StoreConfig.STORE_CURRENCY_INDEX.asInt());

                    SoundEventHelperServer.playSoundClient(playerMP, OxygenSoundEffects.RINGING_COINS.getId());

                    offerData.purchased(TimeHelperServer.getCurrentMillis());
                    playerData.updateOfferDataId(offerData);
                    StoreManagerServer.instance().getPlayersDataContainer().setChanged(true);

                    OxygenMain.network().sendTo(
                            new CPPurchaseSuccessful(offerPersistentId, CurrencyHelperServer.getCurrency(playerUUID, StoreConfig.STORE_CURRENCY_INDEX.asInt())), 
                            playerMP);

                    this.manager.sendStatusMessage(playerMP, EnumStoreStatusMessage.PURCHASE_SUCCESSFUL);

                    if (StoreConfig.ADVANCED_LOGGING.asBoolean())
                        OxygenMain.LOGGER.info("[Store] <{}/{}> [2]: successfuly purchased {}({})",
                                CommonReference.getName(playerMP), 
                                playerUUID,
                                offer.getName(),
                                offerPersistentId);
                    return;
                }
            }
        }
        this.manager.sendStatusMessage(playerMP, EnumStoreStatusMessage.OPERATION_FAILED);
    }

    private void purchaseGift(EntityPlayerMP senderMP, long offerPersistentId, UUID receiverUUID, String message) {
        UUID senderUUID = CommonReference.getPersistentUUID(senderMP);

        message = message.trim();
        if (message.length() > Gift.MAX_MESSAGE_LENGTH)
            message = message.substring(0, Gift.MAX_MESSAGE_LENGTH);

        if (!senderUUID.equals(receiverUUID)
                && OxygenHelperServer.isPlayerOnline(receiverUUID)
                && (StoreConfig.ENABLE_STORE_ACCESS_CLIENTSIDE.asBoolean() || OxygenHelperServer.checkTimeOut(senderUUID, StoreMain.STORE_TIMEOUT_ID))
                && PrivilegesProviderServer.getAsBoolean(senderUUID, EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean())
                && PrivilegesProviderServer.getAsBoolean(receiverUUID, EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean())) {
            StoreOffer offer = StoreManagerServer.instance().getOffersContainer().getOfferByPersistentId(offerPersistentId);
            if (offer != null
                    && offer.isAvailable()
                    && !offer.isFree()) {
                EntityPlayerMP receiverMP = CommonReference.playerByUUID(receiverUUID);

                if (StoreConfig.ADVANCED_LOGGING.asBoolean())
                    OxygenMain.LOGGER.info("[Store] <{}/{}> [1]: trying to purchase {}({}) as gift for player {}/{}...",
                            CommonReference.getName(senderMP), 
                            senderUUID,
                            offer.getName(),
                            offerPersistentId,
                            receiverUUID,
                            CommonReference.getName(receiverMP));

                StorePlayerData 
                senderData = StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(senderUUID),
                receiverData = StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(receiverUUID);
                if (senderData == null)
                    senderData = StoreManagerServer.instance().getPlayersDataContainer().createPlayerData(senderUUID);
                if (receiverData == null)
                    receiverData = StoreManagerServer.instance().getPlayersDataContainer().createPlayerData(receiverUUID);
                OfferData 
                senderOfferData = senderData.getOfferDataByOfferPersistentId(offerPersistentId),
                receiverOfferData = receiverData.getOfferDataByOfferPersistentId(offerPersistentId);
                if (senderOfferData == null) {
                    senderOfferData = new OfferData(
                            senderData.createOfferDataId(TimeHelperServer.getCurrentMillis()), 
                            offerPersistentId);
                    senderData.addOfferData(senderOfferData);
                }
                if (receiverOfferData == null) {
                    receiverOfferData = new OfferData(
                            receiverData.createOfferDataId(TimeHelperServer.getCurrentMillis()), 
                            offerPersistentId);
                    receiverData.addOfferData(receiverOfferData);
                }

                if (receiverData.canReceiveGifts()
                        && senderOfferData.canPurchase(offer, TimeHelperServer.getCurrentMillis())
                        && receiverOfferData.canPurchase(offer, TimeHelperServer.getCurrentMillis())
                        && (offer.isFree() || CurrencyHelperServer.enoughCurrency(senderUUID, offer.isSale() ? offer.getSalePrice() : offer.getPrice(), StoreConfig.STORE_CURRENCY_INDEX.asInt()))) {
                    if (!offer.isFree())
                        CurrencyHelperServer.removeCurrency(senderUUID, offer.isSale() ? offer.getSalePrice() : offer.getPrice(), StoreConfig.STORE_CURRENCY_INDEX.asInt());

                    Gift gift = new Gift(
                            receiverData.createGiftId(TimeHelperServer.getCurrentMillis()),
                            offerPersistentId,
                            senderUUID,
                            CommonReference.getName(senderMP),
                            CommonReference.getName(receiverMP),
                            message,
                            EnumGiftType.PENDING);
                    receiverData.addGift(gift);

                    OxygenHelperServer.addNotification(receiverMP, 
                            new SimpleNotification(StoreMain.STORE_NOTIFICATION_ID, "oxygen_store.notification.simple.receivedGift"));

                    SoundEventHelperServer.playSoundClient(senderMP, OxygenSoundEffects.RINGING_COINS.getId());

                    senderOfferData.purchased(TimeHelperServer.getCurrentMillis());
                    senderData.updateOfferDataId(senderOfferData);
                    StoreManagerServer.instance().getPlayersDataContainer().setChanged(true);

                    OxygenMain.network().sendTo(
                            new CPPurchaseSuccessful(offerPersistentId, CurrencyHelperServer.getCurrency(senderUUID, StoreConfig.STORE_CURRENCY_INDEX.asInt())), 
                            senderMP);

                    this.manager.sendStatusMessage(senderMP, EnumStoreStatusMessage.GIFT_SENT_SUCCESSFULY);

                    if (StoreConfig.ADVANCED_LOGGING.asBoolean())
                        OxygenMain.LOGGER.info("[Store] <{}/{}> [2]: successfuly purchased {}({}) as gift for player {}/{}",
                                CommonReference.getName(senderMP), 
                                senderUUID,
                                offer.getName(),
                                offerPersistentId,
                                receiverUUID,
                                CommonReference.getName(receiverMP));
                    return;
                }
            }
        }
        this.manager.sendStatusMessage(senderMP, EnumStoreStatusMessage.OPERATION_FAILED);
    }

    public void acceptGift(EntityPlayerMP playerMP, long giftId) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if ((StoreConfig.ENABLE_STORE_ACCESS_CLIENTSIDE.asBoolean() || OxygenHelperServer.checkTimeOut(playerUUID, StoreMain.STORE_TIMEOUT_ID))
                && PrivilegesProviderServer.getAsBoolean(playerUUID, EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean())) {
            StorePlayerData playerData = StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(playerUUID);
            if (playerData != null) {
                Gift gift = playerData.getGift(giftId);
                if (gift != null) {
                    StoreOffer offer = StoreManagerServer.instance().getOffersContainer().getOfferByPersistentId(gift.getOfferPersistentId());
                    OfferData offerData = playerData.getOfferDataByOfferPersistentId(offer.getPersistentId());
                    if (offerData == null) {
                        offerData = new OfferData(
                                playerData.createOfferDataId(TimeHelperServer.getCurrentMillis()), 
                                offer.getPersistentId());
                        playerData.addOfferData(offerData);
                    }
                    if (offer != null
                            && offerData.canPurchase(offer, TimeHelperServer.getCurrentMillis())) {
                        if (offer.getGoods().collect(playerMP)) {
                            playerData.removeGift(gift.getId());
                            offerData.purchased(TimeHelperServer.getCurrentMillis());
                            StoreManagerServer.instance().getPlayersDataContainer().setChanged(true);

                            this.manager.sendStatusMessage(playerMP, EnumStoreStatusMessage.GIFT_ACCEPTED_SUCCESSFULY);

                            OxygenMain.network().sendTo(new CPRemoveGift(gift.getId()), playerMP);

                            if (StoreConfig.ADVANCED_LOGGING.asBoolean())
                                OxygenMain.LOGGER.info("[Store] <{}/{}> successfuly accepted {}({}) as gift from player {}/{}",
                                        CommonReference.getName(playerMP), 
                                        playerUUID,
                                        offer.getName(),
                                        offer.getPersistentId(),
                                        gift.getSenderUUID(),
                                        gift.getSenderUsername());
                            return;
                        }
                    }
                }
            }
        }
        this.manager.sendStatusMessage(playerMP, EnumStoreStatusMessage.OPERATION_FAILED);
    }

    public void returnGift(EntityPlayerMP receiverMP, long giftId) {
        UUID receiverUUID = CommonReference.getPersistentUUID(receiverMP);
        if ((StoreConfig.ENABLE_STORE_ACCESS_CLIENTSIDE.asBoolean() || OxygenHelperServer.checkTimeOut(receiverUUID, StoreMain.STORE_TIMEOUT_ID))
                && PrivilegesProviderServer.getAsBoolean(receiverUUID, EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean())) {
            StorePlayerData 
            receiverData = StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(receiverUUID),
            senderData;
            if (receiverData != null) {
                Gift gift = receiverData.getGift(giftId);
                if (gift != null
                        && gift.getType() == EnumGiftType.PENDING
                        && !gift.isSystemGift()) {
                    senderData = StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(gift.getSenderUUID());
                    senderData.addGift(new Gift(
                            senderData.createGiftId(TimeHelperServer.getCurrentMillis()),
                            gift.getOfferPersistentId(),
                            gift.getSenderUUID(),
                            gift.getSenderUsername(),
                            gift.getReceiverUsername(),
                            gift.getMessage(),
                            EnumGiftType.RETURNED));
                    receiverData.removeGift(gift.getId());
                    StoreManagerServer.instance().getPlayersDataContainer().setChanged(true);

                    EntityPlayerMP senderMP = CommonReference.playerByUUID(senderData.getPlayerUUID());
                    if (senderMP != null)
                        OxygenHelperServer.addNotification(senderMP, new SimpleNotification(StoreMain.STORE_NOTIFICATION_ID, "oxygen_store.notification.simple.giftReturned"));

                    this.manager.sendStatusMessage(receiverMP, EnumStoreStatusMessage.GIFT_RETURNED_SUCCESSFULY);

                    OxygenMain.network().sendTo(new CPRemoveGift(gift.getId()), receiverMP);

                    if (StoreConfig.ADVANCED_LOGGING.asBoolean())
                        OxygenMain.LOGGER.info("[Store] <{}/{}> successfuly returned gift ({}) to player {}/{}",
                                CommonReference.getName(receiverMP), 
                                receiverUUID,
                                gift.getOfferPersistentId(),
                                gift.getSenderUUID(),
                                gift.getSenderUsername());
                    return;
                }
            }
        }            
        this.manager.sendStatusMessage(receiverMP, EnumStoreStatusMessage.OPERATION_FAILED);
    }

    public void resendGift(EntityPlayerMP senderMP, long giftId, UUID receiverUUID, String message) {
        UUID senderUUID = CommonReference.getPersistentUUID(senderMP);

        message = message.trim();
        if (message.length() > Gift.MAX_MESSAGE_LENGTH)
            message = message.substring(0, Gift.MAX_MESSAGE_LENGTH);

        if (!senderUUID.equals(receiverUUID)
                && OxygenHelperServer.isPlayerOnline(receiverUUID)
                && (StoreConfig.ENABLE_STORE_ACCESS_CLIENTSIDE.asBoolean() || OxygenHelperServer.checkTimeOut(senderUUID, StoreMain.STORE_TIMEOUT_ID))
                && PrivilegesProviderServer.getAsBoolean(senderUUID, EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean())
                && PrivilegesProviderServer.getAsBoolean(receiverUUID, EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean())) {
            EntityPlayerMP receiverMP = CommonReference.playerByUUID(receiverUUID);

            StorePlayerData 
            senderData = StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(senderUUID),
            receiverData = StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(receiverUUID);
            if (receiverData == null)
                receiverData = StoreManagerServer.instance().getPlayersDataContainer().createPlayerData(receiverUUID);

            if (senderData != null) {
                Gift gift = senderData.getGift(giftId);
                if (gift != null
                        && gift.getType() == EnumGiftType.RETURNED) {
                    receiverData.addGift(new Gift(
                            receiverData.createGiftId(TimeHelperServer.getCurrentMillis()),
                            gift.getOfferPersistentId(),
                            gift.getSenderUUID(),
                            gift.getSenderUsername(),
                            CommonReference.getName(receiverMP),
                            message,
                            EnumGiftType.PENDING));
                    senderData.removeGift(gift.getId());
                    StoreManagerServer.instance().getPlayersDataContainer().setChanged(true);

                    OxygenHelperServer.addNotification(receiverMP, new SimpleNotification(StoreMain.STORE_NOTIFICATION_ID, "oxygen_store.notification.simple.receivedGift"));

                    OxygenMain.network().sendTo(new CPRemoveGift(gift.getId()), senderMP);

                    this.manager.sendStatusMessage(senderMP, EnumStoreStatusMessage.GIFT_SENT_SUCCESSFULY);

                    if (StoreConfig.ADVANCED_LOGGING.asBoolean())
                        OxygenMain.LOGGER.info("[Store] <{}/{}> [2]: successfuly sent gift ({}) to player {}/{}",
                                CommonReference.getName(senderMP), 
                                senderUUID,
                                gift.getOfferPersistentId(),
                                receiverUUID,
                                CommonReference.getName(receiverMP));
                    return;
                }
            }
        }
        this.manager.sendStatusMessage(senderMP, EnumStoreStatusMessage.OPERATION_FAILED);
    }

    //management

    public void reloadOffers(@Nullable EntityPlayerMP playerMP) {
        if (StoreConfig.ENABLE_STORE_MANAGEMENT_INGAME.asBoolean())
            OxygenHelperServer.addRoutineTask(()->this.reload(playerMP));
    }

    private void reload(@Nullable EntityPlayerMP playerMP) {
        OxygenMain.LOGGER.info("[Store] Reloading store offers...");        
        Future future = this.manager.getOffersContainer().loadAsync();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }
        if (playerMP != null) {
            OxygenManagerServer.instance().getDataSyncManager().syncData(playerMP, StoreMain.STORE_OFFERS_DATA_ID);
            this.manager.sendStatusMessage(playerMP, EnumStoreStatusMessage.OFFERS_RELOADED);
        }
        OxygenMain.LOGGER.info("[Store] Store offers reloaded.");
    }
}
