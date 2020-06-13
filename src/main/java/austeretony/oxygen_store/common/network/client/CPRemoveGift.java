package austeretony.oxygen_store.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_store.client.StoreManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPRemoveGift extends Packet {

    private long id;

    public CPRemoveGift() {}

    public CPRemoveGift(long id) {
        this.id = id;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeLong(this.id);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final long id = buffer.readLong();
        OxygenHelperClient.addRoutineTask(()->StoreManagerClient.instance().getStoreOperationsManager().giftRemoved(id));
    }
}
