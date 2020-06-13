package austeretony.oxygen_store.common.network.server;

import java.util.UUID;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.server.StoreManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPGiftOperation extends Packet {

    private EnumType type;

    private long id;

    @Nullable
    private UUID playerUUID;

    @Nullable
    private String message;

    public SPGiftOperation() {}

    public SPGiftOperation(EnumType type, long id, @Nullable UUID playerUUID, @Nullable String message) {
        this.type = type;
        this.id = id;
        this.playerUUID = playerUUID;
        this.message = message;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.type.ordinal());
        buffer.writeLong(this.id);

        if (this.type == EnumType.RESEND) {
            ByteBufUtils.writeUUID(this.playerUUID, buffer);
            ByteBufUtils.writeString(this.message, buffer);
        }
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), StoreMain.STORE_OPERATION_REQUEST_ID)) {
            final int ordinal = buffer.readByte();
            if (ordinal >= 0 && ordinal < EnumType.values().length) {
                final long id = buffer.readLong();

                switch (EnumType.values()[ordinal]) {
                case ACCEPT:
                    OxygenHelperServer.addRoutineTask(
                            ()->StoreManagerServer.instance().getStoreOperationsManager().acceptGift(playerMP, id));
                    break;
                case RETURN:
                    OxygenHelperServer.addRoutineTask(
                            ()->StoreManagerServer.instance().getStoreOperationsManager().returnGift(playerMP, id));
                    break;
                case RESEND:
                    final UUID playerUUID = ByteBufUtils.readUUID(buffer);
                    final String message = ByteBufUtils.readString(buffer);
                    OxygenHelperServer.addRoutineTask(
                            ()->StoreManagerServer.instance().getStoreOperationsManager().resendGift(playerMP, id, playerUUID, message));
                    break;
                }
            }
        }
    }

    public enum EnumType {

        ACCEPT,
        RETURN,
        RESEND
    }
}
