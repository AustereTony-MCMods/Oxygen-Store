package austeretony.oxygen_store.client.gui.menu;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_store.client.StoreMenuManager;
import austeretony.oxygen_store.client.settings.EnumStoreClientSetting;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.main.EnumStorePrivilege;
import austeretony.oxygen_store.common.main.StoreMain;

public class StoreMenuEntry implements OxygenMenuEntry {

    @Override
    public int getId() {
        return StoreMain.STORE_SCREEN_ID;
    }

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_store.gui.store.title");
    }

    @Override
    public int getKeyCode() {
        return StoreConfig.STORE_MENU_KEY.asInt();
    }

    @Override
    public boolean isValid() {
        return StoreConfig.ENABLE_STORE_ACCESS_CLIENTSIDE.asBoolean() 
                && PrivilegesProviderClient.getAsBoolean(EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean())
                && EnumStoreClientSetting.ADD_STORE_MENU.get().asBoolean();
    }

    @Override
    public void open() {
        StoreMenuManager.openStoreMenu();
    }
}

