package austeretony.oxygen_store.common.store;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import austeretony.oxygen_core.client.util.ClientUtils;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronousEntry;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.FilesUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_store.common.store.goods.EnumGoodsType;
import austeretony.oxygen_store.common.store.goods.Goods;
import austeretony.oxygen_store.common.store.goods.icon.EnumIconType;
import austeretony.oxygen_store.common.store.goods.icon.Icon;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;

public class StoreOffer implements PersistentEntry, SynchronousEntry {

    private long persistentId, versionId;

    private EnumGoodsType type;

    private Goods goods;

    private String category, name, description;

    private long price, salePrice;

    private boolean available, canBeGifted;

    private int maxPurchases, purchaseCooldownSeconds, position;

    private EnumWidgetSize widgetSize;

    private byte[] widgetTextureRaw;

    @Nullable
    private List<Icon> icons;

    //client only
    private ResourceLocation widgetTexture;

    public StoreOffer() {}

    @Override
    public long getId() {
        return this.versionId;
    }

    public long getPersistentId() {
        return this.persistentId;
    }

    public EnumGoodsType getGoodsType() {
        return this.type;
    }

    public Goods getGoods() {
        return this.goods;
    }

    public String getCategory() {
        return this.category;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public long getPrice() {
        return this.price;
    }

    public boolean isFree() {
        return this.price == 0L;
    }

    public long getSalePrice() {
        return this.salePrice;
    }

    public boolean isSale() {
        return this.salePrice > 0L;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public boolean canBeGifted() {
        return this.canBeGifted;
    }

    public int getMaxPurchases() {
        return this.maxPurchases;
    }

    public int getPurchasesCooldownSeconds() {
        return this.purchaseCooldownSeconds;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public EnumWidgetSize getWidgetSize() {
        return this.widgetSize;
    }

    @Nullable
    public List<Icon> getIcons() {
        return icons;
    }

    public static StoreOffer fromJson(JsonObject jsonObject) {
        StoreOffer offer = new StoreOffer();
        offer.persistentId = jsonObject.get("persistent_id").getAsLong();
        offer.versionId = jsonObject.get("version_id").getAsLong();
        offer.type = EnumGoodsType.valueOf(jsonObject.get("type").getAsString());
        offer.goods = offer.type.fromJson(jsonObject.get("goods"));
        offer.category = jsonObject.get("category").getAsString();
        offer.name = jsonObject.get("name").getAsString();
        offer.description = jsonObject.get("description").getAsString();
        offer.price = jsonObject.get("price").getAsLong();
        offer.salePrice = jsonObject.get("sale_price").getAsLong();
        offer.available = jsonObject.get("available").getAsBoolean();
        offer.canBeGifted = jsonObject.get("can_be_gifted").getAsBoolean();
        offer.maxPurchases = jsonObject.get("max_purchases").getAsInt();
        offer.purchaseCooldownSeconds = jsonObject.get("purchase_cooldown").getAsInt();

        offer.widgetSize = EnumWidgetSize.valueOf(jsonObject.get("widget_size").getAsString());

        String textureName = jsonObject.get("widget_texture").getAsString();
        offer.widgetTextureRaw = FilesUtils.loadImageBytes(
                CommonReference.getGameFolder() + "/config/oxygen/data/server/store/offers/textures/" + textureName, 
                null);

        JsonArray iconsArray = jsonObject.getAsJsonArray("goods_icons");
        if (iconsArray.size() != 0) {
            offer.icons = new ArrayList<>(iconsArray.size());
            for (JsonElement element : iconsArray)
                offer.icons.add(EnumIconType.loadIcon(element.getAsJsonObject()));
        }

        return offer;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.persistentId, bos);
        StreamUtils.write(this.versionId, bos);
        StreamUtils.write(this.category, bos);
        StreamUtils.write(this.name, bos);
        StreamUtils.write(this.description, bos);
        StreamUtils.write(this.price, bos);
        StreamUtils.write(this.salePrice, bos);
        StreamUtils.write(this.available, bos);
        StreamUtils.write(this.canBeGifted, bos);
        StreamUtils.write(this.maxPurchases, bos);
        StreamUtils.write(this.purchaseCooldownSeconds, bos);

        StreamUtils.write((short) this.position, bos);
        StreamUtils.write((byte) this.widgetSize.ordinal(), bos);

        StreamUtils.write(this.widgetTextureRaw.length, bos);
        StreamUtils.write(this.widgetTextureRaw, bos);

        StreamUtils.write((byte) (this.icons != null ? this.icons.size() : 0), bos);
        if (this.icons != null) {
            for (Icon icon : this.icons)
                EnumIconType.writeIcon(icon, bos);
        }
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.persistentId = StreamUtils.readLong(bis);
        this.versionId = StreamUtils.readLong(bis);
        this.category = StreamUtils.readString(bis);
        this.name = StreamUtils.readString(bis);
        this.description = StreamUtils.readString(bis);
        this.price = StreamUtils.readLong(bis);
        this.salePrice = StreamUtils.readLong(bis);
        this.available = StreamUtils.readBoolean(bis);
        this.canBeGifted = StreamUtils.readBoolean(bis);
        this.maxPurchases = StreamUtils.readInt(bis);
        this.purchaseCooldownSeconds = StreamUtils.readInt(bis);

        this.position = StreamUtils.readShort(bis);
        this.widgetSize = EnumWidgetSize.values()[StreamUtils.readByte(bis)];

        this.widgetTextureRaw = new byte[StreamUtils.readInt(bis)];
        StreamUtils.readBytes(this.widgetTextureRaw, bis);

        int amount = StreamUtils.readByte(bis);
        if (amount > 0) {
            this.icons = new ArrayList<>(amount);
            for (int i = 0; i < amount; i++)
                this.icons.add(EnumIconType.readIcon(bis));
        }
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeLong(this.persistentId);
        buffer.writeLong(this.versionId);
        ByteBufUtils.writeString(this.category, buffer);
        ByteBufUtils.writeString(this.name, buffer);
        ByteBufUtils.writeString(this.description, buffer);
        buffer.writeLong(this.price);
        buffer.writeLong(this.salePrice);
        buffer.writeBoolean(this.available);
        buffer.writeBoolean(this.canBeGifted);
        buffer.writeInt(this.maxPurchases);
        buffer.writeInt(this.purchaseCooldownSeconds);

        buffer.writeShort(this.position);
        buffer.writeByte(this.widgetSize.ordinal());

        buffer.writeInt(this.widgetTextureRaw.length);
        buffer.writeBytes(this.widgetTextureRaw);

        buffer.writeByte(this.icons != null ? this.icons.size() : 0);
        if (this.icons != null) {
            for (Icon icon : this.icons)
                EnumIconType.writeIcon(icon, buffer);
        }
    }

    @Override
    public void read(ByteBuf buffer) {
        this.persistentId = buffer.readLong();
        this.versionId = buffer.readLong();
        this.category = ByteBufUtils.readString(buffer);
        this.name = ByteBufUtils.readString(buffer);
        this.description = ByteBufUtils.readString(buffer);
        this.price = buffer.readLong();
        this.salePrice = buffer.readLong();
        this.available = buffer.readBoolean();
        this.canBeGifted = buffer.readBoolean();
        this.maxPurchases = buffer.readInt();
        this.purchaseCooldownSeconds = buffer.readInt();

        this.position = buffer.readShort();
        this.widgetSize = EnumWidgetSize.values()[buffer.readByte()];

        this.widgetTextureRaw = new byte[buffer.readInt()];
        buffer.readBytes(this.widgetTextureRaw);

        int amount = buffer.readByte();
        if (amount > 0) {
            this.icons = new ArrayList<>(amount);
            for (int i = 0; i < amount; i++)
                this.icons.add(EnumIconType.readIcon(buffer));
        }
    }

    @Nonnull
    public ResourceLocation getWidgetTexture() {
        if (this.widgetTexture == null)
            this.widgetTexture = ClientUtils.getTexturePathFromBytes(this.widgetTextureRaw);
        return this.widgetTexture;
    }
}
