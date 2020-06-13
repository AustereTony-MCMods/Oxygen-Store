package austeretony.oxygen_store.client.sync;

import java.util.Set;

import austeretony.oxygen_core.client.sync.DataSyncHandlerClient;
import austeretony.oxygen_core.client.sync.DataSyncListener;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.common.store.OfferData;

public class OffersDataSyncHandlerClient implements DataSyncHandlerClient<OfferData> {

    @Override
    public int getDataId() {
        return StoreMain.STORE_OFFERS_INFO_DATA_ID;
    }

    @Override
    public Class<OfferData> getDataContainerClass() {
        return OfferData.class;
    }

    @Override
    public Set<Long> getIds() {
        return StoreManagerClient.instance().getPlayerDataContainer().getOffersDataIds();
    }

    @Override
    public void clearData() {
        StoreManagerClient.instance().getPlayerDataContainer().resetOffersData();
    }

    @Override
    public OfferData getEntry(long entryId) {
        return StoreManagerClient.instance().getPlayerDataContainer().getOfferData(entryId);
    }

    @Override
    public void addEntry(OfferData entry) {
        StoreManagerClient.instance().getPlayerDataContainer().addOfferData(entry);
    }

    @Override
    public void save() {
        StoreManagerClient.instance().getPlayerDataContainer().setChanged(true);
    }

    @Override
    public DataSyncListener getSyncListener() {
        return (updated)->StoreManagerClient.instance().getMenuManager().offersDataSynchronized();
    }
}
