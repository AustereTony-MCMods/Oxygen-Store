package austeretony.oxygen_store.client.sync;

import java.util.Set;

import austeretony.oxygen_core.client.sync.DataSyncHandlerClient;
import austeretony.oxygen_core.client.sync.DataSyncListener;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.common.store.gift.Gift;

public class GiftsSyncHandlerClient implements DataSyncHandlerClient<Gift> {

    @Override
    public int getDataId() {
        return StoreMain.STORE_GIFTS_DATA_ID;
    }

    @Override
    public Class<Gift> getDataContainerClass() {
        return Gift.class;
    }

    @Override
    public Set<Long> getIds() {
        return StoreManagerClient.instance().getPlayerDataContainer().getGiftsIds();
    }

    @Override
    public void clearData() {
        StoreManagerClient.instance().getPlayerDataContainer().resetGiftsData();
    }

    @Override
    public Gift getEntry(long entryId) {
        return StoreManagerClient.instance().getPlayerDataContainer().getGift(entryId);
    }

    @Override
    public void addEntry(Gift entry) {
        StoreManagerClient.instance().getPlayerDataContainer().addGift(entry);
    }

    @Override
    public void save() {
        StoreManagerClient.instance().getPlayerDataContainer().setChanged(true);
    }

    @Override
    public DataSyncListener getSyncListener() {
        return (updated)->StoreManagerClient.instance().getMenuManager().giftsSynchronized();
    }
}
