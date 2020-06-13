package austeretony.oxygen_store.server.sync;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen_core.server.sync.DataSyncHandlerServer;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.common.store.OfferData;
import austeretony.oxygen_store.server.StoreManagerServer;

public class OffersDataSyncHandlerServer implements DataSyncHandlerServer<OfferData> {

    @Override
    public int getDataId() {
        return StoreMain.STORE_OFFERS_INFO_DATA_ID;
    }

    @Override
    public boolean allowSync(UUID playerUUID) {
        return StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(playerUUID) != null;
    }

    @Override
    public Set<Long> getIds(UUID playerUUID) {
        return StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(playerUUID).getOffersDataIds();
    }

    @Override
    public OfferData getEntry(UUID playerUUID, long entryId) {
        return StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(playerUUID).getOfferData(entryId);
    }
}
