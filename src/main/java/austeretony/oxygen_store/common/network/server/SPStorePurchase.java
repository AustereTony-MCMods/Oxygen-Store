package austeretony.oxygen_store.common.network.server;

import java.util.UUID;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.common.store.EnumPurchaseType;
import austeretony.oxygen_store.server.StoreManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPStorePurchase extends Packet {

    private EnumPurchaseType type;

    private long offerPersistentId;

    @Nullable
    private UUID playerUUID;

    @Nullable
    private String message;

    public SPStorePurchase() {}

    public SPStorePurchase(EnumPurchaseType type, long offerPersistentId, @Nullable UUID playerUUID, @Nullable String message) {
        this.type = type;
        this.offerPersistentId = offerPersistentId;
        this.playerUUID = playerUUID;
        this.message = message;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.type.ordinal());
        buffer.writeLong(this.offerPersistentId);

        if (this.type == EnumPurchaseType.GIFT) {
            ByteBufUtils.writeUUID(this.playerUUID, buffer);
            ByteBufUtils.writeString(this.message, buffer);
        }
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), StoreMain.STORE_OPERATION_REQUEST_ID)) {
            final int ordinal = buffer.readByte();
            if (ordinal >= 0 && ordinal < EnumPurchaseType.values().length) {
                final long offerPersistentId = buffer.readLong();

                EnumPurchaseType type = EnumPurchaseType.values()[ordinal];
                if (type == EnumPurchaseType.GIFT) {
                    final UUID playerUUID = ByteBufUtils.readUUID(buffer);
                    final String message = ByteBufUtils.readString(buffer);
                    OxygenHelperServer.addRoutineTask(
                            ()->StoreManagerServer.instance().getStoreOperationsManager().purchase(playerMP, type, offerPersistentId, playerUUID, message));

                } else
                    OxygenHelperServer.addRoutineTask(
                            ()->StoreManagerServer.instance().getStoreOperationsManager().purchase(playerMP, type, offerPersistentId, null, null));
            }
        }
    }
}
