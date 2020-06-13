package austeretony.oxygen_store.server.event;

import austeretony.oxygen_core.server.api.event.OxygenPrivilegesLoadedEvent;
import austeretony.oxygen_core.server.api.event.OxygenWorldLoadedEvent;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.server.StoreManagerServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StoreEventsServer {

    @SubscribeEvent
    public void onPrivilegesLoaded(OxygenPrivilegesLoadedEvent event) {
        StoreMain.addDefaultPrivileges();
    }

    @SubscribeEvent
    public void onWorldLoaded(OxygenWorldLoadedEvent event) {
        StoreManagerServer.instance().worldLoaded();
    }
}
