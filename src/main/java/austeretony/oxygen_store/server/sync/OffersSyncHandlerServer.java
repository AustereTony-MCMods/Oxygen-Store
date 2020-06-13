package austeretony.oxygen_store.server.sync;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.PrivilegesProviderServer;
import austeretony.oxygen_core.server.sync.DataSyncHandlerServer;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.main.EnumStorePrivilege;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.common.store.StoreOffer;
import austeretony.oxygen_store.server.StoreManagerServer;

public class OffersSyncHandlerServer implements DataSyncHandlerServer<StoreOffer> {

    @Override
    public int getDataId() {
        return StoreMain.STORE_OFFERS_DATA_ID;
    }

    @Override
    public boolean allowSync(UUID playerUUID) {
        return (StoreConfig.ENABLE_STORE_ACCESS_CLIENTSIDE.asBoolean() || OxygenHelperServer.checkTimeOut(playerUUID, StoreMain.STORE_TIMEOUT_ID))
                && PrivilegesProviderServer.getAsBoolean(playerUUID, EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean());
    }

    @Override
    public Set<Long> getIds(UUID playerUUID) {
        return StoreManagerServer.instance().getOffersContainer().getOffersIds();
    }

    @Override
    public StoreOffer getEntry(UUID playerUUID, long entryId) {
        return StoreManagerServer.instance().getOffersContainer().getOffer(entryId);
    }
}
