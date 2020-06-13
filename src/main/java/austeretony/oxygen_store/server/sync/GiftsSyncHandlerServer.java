package austeretony.oxygen_store.server.sync;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen_core.server.sync.DataSyncHandlerServer;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.common.store.gift.Gift;
import austeretony.oxygen_store.server.StoreManagerServer;

public class GiftsSyncHandlerServer implements DataSyncHandlerServer<Gift> {

    @Override
    public int getDataId() {
        return StoreMain.STORE_GIFTS_DATA_ID;
    }

    @Override
    public boolean allowSync(UUID playerUUID) {
        return StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(playerUUID) != null;
    }

    @Override
    public Set<Long> getIds(UUID playerUUID) {
        return StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(playerUUID).getGiftsIds();
    }

    @Override
    public Gift getEntry(UUID playerUUID, long entryId) {
        return StoreManagerServer.instance().getPlayersDataContainer().getPlayerData(playerUUID).getGift(entryId);
    }
}
