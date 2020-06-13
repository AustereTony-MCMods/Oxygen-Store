package austeretony.oxygen_store.client.sync;

import java.util.Set;

import austeretony.oxygen_core.client.sync.DataSyncHandlerClient;
import austeretony.oxygen_core.client.sync.DataSyncListener;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.common.store.StoreOffer;

public class OffersSyncHandlerClient implements DataSyncHandlerClient<StoreOffer> {

    @Override
    public int getDataId() {
        return StoreMain.STORE_OFFERS_DATA_ID;
    }

    @Override
    public Class<StoreOffer> getDataContainerClass() {
        return StoreOffer.class;
    }

    @Override
    public Set<Long> getIds() {
        return StoreManagerClient.instance().getOffersContainer().getOfferIds();
    }

    @Override
    public void clearData() {
        StoreManagerClient.instance().getOffersContainer().reset();
    }

    @Override
    public StoreOffer getEntry(long entryId) {
        return StoreManagerClient.instance().getOffersContainer().getOffer(entryId);
    }

    @Override
    public void addEntry(StoreOffer entry) {
        StoreManagerClient.instance().getOffersContainer().addOffer(entry);
    }

    @Override
    public void save() {
        StoreManagerClient.instance().getOffersContainer().setChanged(true);
    }

    @Override
    public DataSyncListener getSyncListener() {
        return (updated)->StoreManagerClient.instance().getMenuManager().offersSynchronized();
    }
}
