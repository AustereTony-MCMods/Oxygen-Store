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
import austeretony.oxygen_store.common.store.StoreOffer;

public class StoreOffersContainerClient extends AbstractPersistentData {

    private final Map<Long, StoreOffer> offers = new ConcurrentHashMap<>();

    protected StoreOffersContainerClient() {}

    public Set<Long> getOfferIds() {
        return this.offers.keySet();
    }

    public Collection<StoreOffer> getOffers() {
        return this.offers.values();
    }

    @Nullable
    public StoreOffer getOffer(long offerId) {
        return this.offers.get(offerId);
    }
    
    @Nullable
    public StoreOffer getOfferByPersistentId(long persistentId) {
        for (StoreOffer offer : this.offers.values())
            if (offer.getPersistentId() == persistentId)
                return offer;
        return null;
    }

    public void addOffer(StoreOffer offer) {
        this.offers.put(offer.getId(), offer);
    }

    public void removeOffer(long offerId) {
        this.offers.remove(offerId);
    }

    @Override
    public String getDisplayName() {
        return "store:offers_client";
    }

    @Override
    public String getPath() {
        return OxygenHelperClient.getDataFolder() + "/client/world/store/offers_client.dat";
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        int amount = StreamUtils.readInt(bis);
        StoreOffer offer;
        for (int i = 0; i < amount; i++) {
            offer = new StoreOffer();
            offer.read(bis);
            this.addOffer(offer);
        }
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.offers.size(), bos);
        for (StoreOffer offer : this.offers.values())
            offer.write(bos);
    }

    @Override
    public void reset() {
        this.offers.clear();
    }
}
