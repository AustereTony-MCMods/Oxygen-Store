package austeretony.oxygen_store.common.store.gift;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronousEntry;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_store.common.config.StoreConfig;
import io.netty.buffer.ByteBuf;

public class Gift implements PersistentEntry, SynchronousEntry {

    public static final int MAX_MESSAGE_LENGTH = 200;

    private long id, offerPersistentId;

    private UUID senderUUID;

    private String senderUsername, receiverUsername, message;

    private EnumGiftType type;

    public Gift() {}

    public Gift(long id, long offerPersistentId, UUID senderUUID, String senderUsername, String receiverUsername, String message, EnumGiftType type) {
        this.id = id;
        this.offerPersistentId = offerPersistentId;
        this.senderUUID = senderUUID;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.message = message;
        this.type = type;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public long getOfferPersistentId() {
        return this.offerPersistentId;
    }

    public UUID getSenderUUID() {
        return this.senderUUID;
    }

    public boolean isSystemGift() {
        return this.senderUUID.equals(OxygenMain.SYSTEM_UUID);
    }

    public String getSenderUsername() {
        return this.senderUsername;
    }

    public String getReceiverUsername() {
        return this.receiverUsername;
    }

    public String getMessage() {
        return this.message;
    }

    public EnumGiftType getType() {
        return this.type;
    }

    public boolean isExpired(long currentTimeMillis) {
        int expiresIn = StoreConfig.GIFT_EXPIRE_TIME_HOURS.asInt();
        if (expiresIn == - 1)
            return false;
        return currentTimeMillis - this.id > expiresIn * TimeUnit.HOURS.toMillis(1L);
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.id, bos);
        StreamUtils.write(this.offerPersistentId, bos);
        StreamUtils.write(this.senderUUID, bos);
        StreamUtils.write(this.senderUsername, bos);
        StreamUtils.write(this.receiverUsername, bos);
        StreamUtils.write(this.message, bos);
        StreamUtils.write((byte) this.type.ordinal(), bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.id = StreamUtils.readLong(bis);
        this.offerPersistentId = StreamUtils.readLong(bis);
        this.senderUUID = StreamUtils.readUUID(bis);
        this.senderUsername = StreamUtils.readString(bis);
        this.receiverUsername = StreamUtils.readString(bis);
        this.message = StreamUtils.readString(bis);
        this.type = EnumGiftType.values()[StreamUtils.readByte(bis)];
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeLong(this.id);
        buffer.writeLong(this.offerPersistentId);
        ByteBufUtils.writeUUID(this.senderUUID, buffer);
        ByteBufUtils.writeString(this.senderUsername, buffer);
        ByteBufUtils.writeString(this.receiverUsername, buffer);
        ByteBufUtils.writeString(this.message, buffer);
        buffer.writeByte(this.type.ordinal());
    }

    @Override
    public void read(ByteBuf buffer) {
        this.id = buffer.readLong();
        this.offerPersistentId = buffer.readLong();
        this.senderUUID = ByteBufUtils.readUUID(buffer);
        this.senderUsername = ByteBufUtils.readString(buffer);
        this.receiverUsername = ByteBufUtils.readString(buffer);
        this.message = ByteBufUtils.readString(buffer);
        this.type = EnumGiftType.values()[buffer.readByte()];
    }
}
