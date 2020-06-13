package austeretony.oxygen_store.client.input;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_store.client.StoreMenuManager;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.main.EnumStorePrivilege;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class StoreKeyHandler {

    private KeyBinding storeMenuKeybinding;

    public StoreKeyHandler() {        
        if (StoreConfig.ENABLE_STORE_MENU_KEY.asBoolean() && !OxygenGUIHelper.isOxygenMenuEnabled())
            ClientReference.registerKeyBinding(this.storeMenuKeybinding = new KeyBinding("key.oxygen_store.storeMenu", StoreConfig.STORE_MENU_KEY.asInt(), "Oxygen"));
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {        
        if (this.storeMenuKeybinding != null && this.storeMenuKeybinding.isPressed())
            if (StoreConfig.ENABLE_STORE_ACCESS_CLIENTSIDE.asBoolean() 
                    && PrivilegesProviderClient.getAsBoolean(EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean()))
                StoreMenuManager.openStoreMenu();
    }

    public KeyBinding getStoreMenuKeybinding() {
        return this.storeMenuKeybinding;
    }
}
