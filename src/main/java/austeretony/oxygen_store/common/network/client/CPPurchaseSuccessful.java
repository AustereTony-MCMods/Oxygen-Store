package austeretony.oxygen_store.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_store.client.StoreManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPPurchaseSuccessful extends Packet {

    private long offerPersistentId, balance;

    public CPPurchaseSuccessful() {}

    public CPPurchaseSuccessful(long offerPersistentId, long balance) {
        this.offerPersistentId = offerPersistentId;
        this.balance = balance;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeLong(this.offerPersistentId);
        buffer.writeLong(this.balance);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final long 
        offerPersistentId = buffer.readLong(),
        balance = buffer.readLong();
        OxygenHelperClient.addRoutineTask(()->StoreManagerClient.instance().getStoreOperationsManager().purchaseSuccessful(offerPersistentId, balance));
    }
}
