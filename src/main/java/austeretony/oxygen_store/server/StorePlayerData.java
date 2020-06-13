package austeretony.oxygen_store.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.store.OfferData;
import austeretony.oxygen_store.common.store.gift.Gift;

public class StorePlayerData {

    private final UUID playerUUID;

    private final Map<Long, OfferData> offersData = new ConcurrentHashMap<>();

    private final Map<Long, Gift> gifts = new ConcurrentHashMap<>();

    public StorePlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public Set<Long> getOffersDataIds() {
        return this.offersData.keySet();
    }

    public void addOfferData(OfferData offerData) {
        this.offersData.put(offerData.getId(), offerData);
    }

    public void updateOfferDataId(OfferData offerData) {
        this.offersData.remove(offerData.getId());
        offerData.setId(this.createOfferDataId(offerData.getId()));
        this.addOfferData(offerData);
    }

    @Nullable
    public OfferData getOfferData(long id) {
        return this.offersData.get(id);
    }

    @Nullable
    public OfferData getOfferDataByOfferPersistentId(long offerPersistentId) {
        for (OfferData offerData : this.offersData.values())
            if (offerData.getOfferPersistentId() == offerPersistentId)
                return offerData;
        return null;
    }

    public Set<Long> getGiftsIds() {
        return this.gifts.keySet();
    }

    public Collection<Gift> getGifts() {
        return this.gifts.values();
    }

    public void addGift(Gift gift) {
        this.gifts.put(gift.getId(), gift);
    }

    @Nullable
    public Gift getGift(long id) {
        return this.gifts.get(id);
    }

    public void removeGift(long id) {
        this.gifts.remove(id);
    }

    public boolean canReceiveGifts() {
        return this.gifts.size() < StoreConfig.GIFTS_INVENTORY_SIZE.asInt();
    }

    public long createOfferDataId(long seed) {
        long id = ++seed;
        while (this.offersData.containsKey(id))
            id++;
        return id;
    }

    public long createGiftId(long seed) {
        long id = ++seed;
        while (this.gifts.containsKey(id))
            id++;
        return id;
    }

    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.playerUUID, bos);

        StreamUtils.write((short) this.offersData.size(), bos);
        for (OfferData offerData : this.offersData.values()) 
            offerData.write(bos);

        StreamUtils.write((short) this.gifts.size(), bos);
        for (Gift gift : this.gifts.values()) 
            gift.write(bos);
    }

    public static StorePlayerData read(BufferedInputStream bis) throws IOException {
        StorePlayerData playerData = new StorePlayerData(StreamUtils.readUUID(bis));
        int amount = StreamUtils.readShort(bis);
        OfferData offerData;
        for (int i = 0; i < amount; i++) {
            offerData = new OfferData();
            offerData.read(bis);
            playerData.offersData.put(offerData.getId(), offerData);
        }

        amount = StreamUtils.readShort(bis);
        Gift gift;
        for (int i = 0; i < amount; i++) {
            gift = new Gift();
            gift.read(bis);
            playerData.gifts.put(gift.getId(), gift);
        }
        return playerData;
    }
}
