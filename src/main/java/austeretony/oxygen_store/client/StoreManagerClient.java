package austeretony.oxygen_store.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_store.client.input.StoreKeyHandler;

public class StoreManagerClient {

    private static StoreManagerClient instance;

    private final StoreOffersContainerClient offersContainer = new StoreOffersContainerClient();

    private final StorePlayerDataContainerClient playerDataContainer = new StorePlayerDataContainerClient();

    private final StoreOperationsManagerClient operationsManager;

    private final StoreMenuManager menuManager;

    private final StoreKeyHandler keyHandler = new StoreKeyHandler();

    private StoreManagerClient() {
        this.operationsManager = new StoreOperationsManagerClient(this);
        this.menuManager = new StoreMenuManager(this);
        CommonReference.registerEvent(this.keyHandler);
    }

    private void registerPersistentData() {
        OxygenHelperClient.registerPersistentData(this.offersContainer);
        OxygenHelperClient.registerPersistentData(this.playerDataContainer);
    }

    public static void create() {
        if (instance == null) {
            instance = new StoreManagerClient();
            instance.registerPersistentData();
        }
    }

    public static StoreManagerClient instance() {
        return instance;
    }

    public StoreOffersContainerClient getOffersContainer() {
        return this.offersContainer;
    }

    public StorePlayerDataContainerClient getPlayerDataContainer() {
        return this.playerDataContainer;
    }

    public StoreOperationsManagerClient getStoreOperationsManager() {
        return this.operationsManager;
    }

    public StoreMenuManager getMenuManager() {
        return this.menuManager;
    }

    public StoreKeyHandler getKeyHandler() {
        return this.keyHandler;
    }

    public void worldLoaded() {
        OxygenHelperClient.loadPersistentDataAsync(this.offersContainer);
        OxygenHelperClient.loadPersistentDataAsync(this.playerDataContainer);
    }
}
