package austeretony.oxygen_store.client;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_store.client.gui.store.StoreMenuScreen;

public class StoreMenuManager {

    private final StoreManagerClient manager;

    protected StoreMenuManager(StoreManagerClient manager) {
        this.manager = manager;
    }

    public static void openStoreMenu() {
        ClientReference.displayGuiScreen(new StoreMenuScreen());
    }

    public static void openStoreMenuDelegated() {
        ClientReference.delegateToClientThread(StoreMenuManager::openStoreMenu);
    }

    public void offersSynchronized() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((StoreMenuScreen) ClientReference.getCurrentScreen()).offersSynchronized();
        });
    }

    public void offersDataSynchronized() { 
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((StoreMenuScreen) ClientReference.getCurrentScreen()).offersDataSynchronized();
        });
    }

    public void giftsSynchronized() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((StoreMenuScreen) ClientReference.getCurrentScreen()).giftsSynchronized();
        });
    }

    public void purchaseSuccessful(long offerPersistentId, long balance) {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((StoreMenuScreen) ClientReference.getCurrentScreen()).purchaseSuccessful(offerPersistentId, balance);
        });
    }
    
    public void giftRemoved(long id) {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((StoreMenuScreen) ClientReference.getCurrentScreen()).giftRemoved(id);
        });
    }

    public static boolean isMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof StoreMenuScreen;
    }
}
