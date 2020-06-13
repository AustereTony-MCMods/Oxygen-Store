package austeretony.oxygen_store.common.store;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronousEntry;
import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;

public class OfferData implements PersistentEntry, SynchronousEntry {

    private long id, offerPersistentId;

    private int purchasesAmount;

    private long lastPurchaseTimeMillis;

    public OfferData() {}

    public OfferData(long id, long offerPersistentId) {
        this.id = id;
        this.offerPersistentId = offerPersistentId;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOfferPersistentId() {
        return this.offerPersistentId;
    }

    public int getPurchasesAmount() {
        return this.purchasesAmount;
    }

    public long getLastPurchaseTimeMillis() {
        return this.lastPurchaseTimeMillis;
    }

    public boolean canPurchase(StoreOffer offer, long currentTimeMillis) {
        return ((offer.getMaxPurchases() == - 1 || this.purchasesAmount < offer.getMaxPurchases()) 
                && (offer.getPurchasesCooldownSeconds() == 0 || TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis - this.lastPurchaseTimeMillis) >= offer.getPurchasesCooldownSeconds()));
    }

    public void purchased(long purchaseTimeMillis) {
        this.purchasesAmount++;
        this.lastPurchaseTimeMillis = purchaseTimeMillis;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.id, bos);
        StreamUtils.write(this.offerPersistentId, bos);
        StreamUtils.write(this.purchasesAmount, bos);
        StreamUtils.write(this.lastPurchaseTimeMillis, bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.id = StreamUtils.readLong(bis);
        this.offerPersistentId = StreamUtils.readLong(bis);
        this.purchasesAmount = StreamUtils.readInt(bis);
        this.lastPurchaseTimeMillis = StreamUtils.readLong(bis);
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeLong(this.id);
        buffer.writeLong(this.offerPersistentId);
        buffer.writeInt(this.purchasesAmount);
        buffer.writeLong(this.lastPurchaseTimeMillis);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.id = buffer.readLong();
        this.offerPersistentId = buffer.readLong();
        this.purchasesAmount = buffer.readInt();
        this.lastPurchaseTimeMillis = buffer.readLong();
    }
}
