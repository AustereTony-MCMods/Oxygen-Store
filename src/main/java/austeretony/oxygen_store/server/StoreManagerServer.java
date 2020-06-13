package austeretony.oxygen_store.server;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_store.common.main.EnumStoreStatusMessage;
import austeretony.oxygen_store.common.main.StoreMain;
import net.minecraft.entity.player.EntityPlayerMP;

public class StoreManagerServer {

    private static StoreManagerServer instance;

    private final StoreOffersContainerServer offersContainer = new StoreOffersContainerServer();

    private final StorePlayersDataContainerServer playersDataContainer = new StorePlayersDataContainerServer();

    private final StoreOperationsManagerServer operationsManager;

    private StoreManagerServer() {
        this.operationsManager = new StoreOperationsManagerServer(this);
    }

    private void registerPersistentData() {
        OxygenHelperServer.registerPersistentData(this.playersDataContainer);
    }

    private void scheduleRepeatableProcesses() {
        OxygenHelperServer.getSchedulerExecutorService().scheduleAtFixedRate(this.operationsManager::process, 1L, 1L, TimeUnit.SECONDS);
    }

    public static void create() {
        if (instance == null) {
            instance = new StoreManagerServer();
            instance.registerPersistentData();
            instance.scheduleRepeatableProcesses();
        }
    }

    public static StoreManagerServer instance() {
        return instance;
    }

    public StoreOffersContainerServer getOffersContainer() {
        return this.offersContainer;
    }

    public StorePlayersDataContainerServer getPlayersDataContainer() {
        return this.playersDataContainer;
    }

    public StoreOperationsManagerServer getStoreOperationsManager() {
        return this.operationsManager;
    }

    public void worldLoaded() {
        this.offersContainer.loadAsync();
        OxygenHelperServer.loadPersistentDataAsync(this.playersDataContainer);
    }

    public void sendStatusMessage(EntityPlayerMP playerMP, EnumStoreStatusMessage status) {
        OxygenHelperServer.sendStatusMessage(playerMP, StoreMain.STORE_MOD_INDEX, status.ordinal());
    }
}
