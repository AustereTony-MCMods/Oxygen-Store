package austeretony.oxygen_store.client;

import java.util.UUID;

import austeretony.oxygen_core.client.api.TimeHelperClient;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_store.common.network.server.SPGiftOperation;
import austeretony.oxygen_store.common.network.server.SPStorePurchase;
import austeretony.oxygen_store.common.store.EnumPurchaseType;
import austeretony.oxygen_store.common.store.OfferData;

public class StoreOperationsManagerClient {

    private final StoreManagerClient manager;

    protected StoreOperationsManagerClient(StoreManagerClient manager) {
        this.manager = manager;
    }

    public void purchaseSelfSynced(long offerPersistentId) {
        OxygenMain.network().sendToServer(new SPStorePurchase(EnumPurchaseType.SELF, offerPersistentId, null, null));
    }

    public void purchaseGiftSynced(long offerPersistentId, UUID playerUUID, String message) {
        OxygenMain.network().sendToServer(new SPStorePurchase(EnumPurchaseType.GIFT, offerPersistentId, playerUUID, message));
    }

    public void purchaseSuccessful(long offerPersistentId, long balance) {
        OfferData offerData = this.manager.getPlayerDataContainer().getOfferDataByOfferPersistentId(offerPersistentId);
        long serverTimeMillis = TimeHelperClient.getServerZonedDateTime().toInstant().toEpochMilli();
        if (offerData == null) {
            offerData = new OfferData(
                    serverTimeMillis,
                    offerPersistentId);
            this.manager.getPlayerDataContainer().addOfferData(offerData);
        } else
            this.manager.getPlayerDataContainer().updateOfferDataId(offerData);
        offerData.purchased(serverTimeMillis);

        this.manager.getMenuManager().purchaseSuccessful(offerPersistentId, balance);
    }

    public void acceptGiftSynced(long id) {
        OxygenMain.network().sendToServer(new SPGiftOperation(SPGiftOperation.EnumType.ACCEPT, id, null, null));
    }

    public void returnGiftSynced(long id) {
        OxygenMain.network().sendToServer(new SPGiftOperation(SPGiftOperation.EnumType.RETURN, id, null, null));
    }

    public void resendGiftSynced(long id, UUID playerUUID, String message) {
        OxygenMain.network().sendToServer(new SPGiftOperation(SPGiftOperation.EnumType.RESEND, id, playerUUID, message));
    }

    public void giftRemoved(long id) {
        this.manager.getPlayerDataContainer().removeGift(id);
        this.manager.getPlayerDataContainer().setChanged(true);

        this.manager.getMenuManager().giftRemoved(id);
    }
}
