package austeretony.oxygen_store.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_store.client.StoreMenuManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPOpenStoreMenu extends Packet {

    public CPOpenStoreMenu() {}

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {}

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        OxygenHelperClient.addRoutineTask(StoreMenuManager::openStoreMenuDelegated);
    }
}
