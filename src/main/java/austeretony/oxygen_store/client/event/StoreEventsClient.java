package austeretony.oxygen_store.client.event;

import austeretony.oxygen_core.client.api.event.OxygenClientInitEvent;
import austeretony.oxygen_store.client.StoreManagerClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StoreEventsClient {

    @SubscribeEvent
    public void onClientInit(OxygenClientInitEvent event) {
        StoreManagerClient.instance().worldLoaded();
    }
}
