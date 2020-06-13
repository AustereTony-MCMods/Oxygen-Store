package austeretony.oxygen_store.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_store.common.store.OfferData;
import austeretony.oxygen_store.common.store.gift.Gift;
import io.netty.util.internal.ConcurrentSet;

public class StorePlayerDataContainerClient extends AbstractPersistentData {

    private final Map<Long, OfferData> offersData = new ConcurrentHashMap<>();

    private final Map<Long, Gift> gifts = new ConcurrentHashMap<>();

    private final Set<Long> checkedGifts = new ConcurrentSet<>();

    protected StorePlayerDataContainerClient() {}

    public Set<Long> getOffersDataIds() {
        return this.offersData.keySet();
    }

    public void addOfferData(OfferData offerData) {
        this.offersData.put(offerData.getId(), offerData);
    }

    public void updateOfferDataId(OfferData offerData) {
        this.offersData.remove(offerData.getId());
        offerData.setId(offerData.getId() + 1L);
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

    @Nullable
    public Gift getGift(long id) {
        return this.gifts.get(id);
    }

    public void addGift(Gift gift) {
        this.gifts.put(gift.getId(), gift);
    }

    public void removeGift(long id) {
        this.gifts.remove(id);
        this.checkedGifts.remove(id);
    }

    public boolean isGiftChecked(long messageId) {
        return this.checkedGifts.contains(messageId);
    }

    public void markGiftChecked(long messageId) {
        this.checkedGifts.add(messageId);
    }

    public void removeGiftCheckedMark(long messageId) {
        this.checkedGifts.remove(messageId);
    }

    @Override
    public String getDisplayName() {
        return "store:player_data_client";
    }

    @Override
    public String getPath() {
        return OxygenHelperClient.getDataFolder() + "/client/players/" + OxygenHelperClient.getPlayerUUID() + "/store/player_data.dat";
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.offersData.size(), bos);
        for (OfferData offerData : this.offersData.values()) 
            offerData.write(bos);

        StreamUtils.write((short) this.gifts.size(), bos);
        for (Gift gift : this.gifts.values())
            gift.write(bos);

        StreamUtils.write((short) this.checkedGifts.size(), bos);
        for (long messageId : this.checkedGifts)
            StreamUtils.write(messageId, bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        int amount = StreamUtils.readShort(bis);
        OfferData offerData;
        for (int i = 0; i < amount; i++) {
            offerData = new OfferData();
            offerData.read(bis);
            this.addOfferData(offerData);
        }

        amount = StreamUtils.readShort(bis);      
        Gift gift;
        for (int i = 0; i < amount; i++) {
            gift = new Gift();
            gift.read(bis);
            this.addGift(gift);
        }

        amount = StreamUtils.readShort(bis);
        for (int i = 0; i < amount; i++)
            this.checkedGifts.add(StreamUtils.readLong(bis));
    }

    @Override
    public void reset() {
        this.offersData.clear();
        this.gifts.clear();
    }

    public void resetOffersData() {
        this.offersData.clear();
    }

    public void resetGiftsData() {
        this.gifts.clear();
    }
}
