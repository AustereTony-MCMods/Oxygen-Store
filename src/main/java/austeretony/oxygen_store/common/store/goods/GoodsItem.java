package austeretony.oxygen_store.common.store.goods;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.sound.OxygenSoundEffects;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.api.InventoryProviderServer;
import austeretony.oxygen_core.server.api.SoundEventHelperServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class GoodsItem implements Goods {

    private final Map<ItemStackWrapper, Integer> items = new LinkedHashMap<>();

    private GoodsItem() {}

    public static Goods fromJson(JsonElement element) {
        JsonArray itemsArray = element.getAsJsonArray();
        GoodsItem goods = new GoodsItem();
        JsonObject itemObject;
        for (JsonElement jsonElement : itemsArray) {
            itemObject = jsonElement.getAsJsonObject();
            goods.items.put(
                    ItemStackWrapper.fromJson(itemObject.get("itemstack").getAsJsonObject()), 
                    itemObject.get("amount").getAsInt());
        }
        return goods;
    }

    @Override
    public boolean collect(EntityPlayerMP playerMP) {
        if (InventoryProviderServer.getPlayerInventory().getEmptySlotsAmount(playerMP) < this.items.size()) {
            OxygenManagerServer.instance().sendStatusMessage(playerMP, EnumOxygenStatusMessage.INVENTORY_FULL);
            return false;//TODO Need more reliable free space check with honoring items quantity 
        }

        for (Map.Entry<ItemStackWrapper, Integer> entry : this.items.entrySet())
            InventoryProviderServer.getPlayerInventory().addItem(playerMP, entry.getKey(), entry.getValue());

        SoundEventHelperServer.playSoundClient(playerMP, OxygenSoundEffects.INVENTORY_OPERATION.getId());
        return true;
    }
}
