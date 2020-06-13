package austeretony.oxygen_store.server;

import java.util.UUID;

import javax.annotation.Nullable;

import austeretony.oxygen_store.common.store.EnumPurchaseType;
import net.minecraft.entity.player.EntityPlayerMP;

public class QueuedStorePurchase {

    final EntityPlayerMP playerMP;

    final EnumPurchaseType type;

    final long offerPersistentId;

    @Nullable
    final UUID receiverUUID;

    @Nullable
    final String message;

    protected QueuedStorePurchase(EntityPlayerMP playerMP, EnumPurchaseType type, long offerPersistentId, 
            @Nullable UUID receiverUUID, @Nullable String message) {
        this.playerMP = playerMP;
        this.type = type;
        this.offerPersistentId = offerPersistentId;
        this.receiverUUID = receiverUUID;
        this.message = message;
    }
}
