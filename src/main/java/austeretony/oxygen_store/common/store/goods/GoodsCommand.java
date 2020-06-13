package austeretony.oxygen_store.common.store.goods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import net.minecraft.entity.player.EntityPlayerMP;

public class GoodsCommand implements Goods {

    private final String[] commands;

    private GoodsCommand(int commandsAmount) {
        this.commands = new String[commandsAmount];
    }

    public static Goods fromJson(JsonElement element) {
        JsonArray commandsArray = element.getAsJsonArray();
        GoodsCommand goods = new GoodsCommand(commandsArray.size());
        int index = 0;
        for (JsonElement jsonElement : commandsArray)
            goods.commands[index++] = jsonElement.getAsString();
        return goods;
    }

    @Override
    public boolean collect(EntityPlayerMP playerMP) {
        int result;
        for (String command : this.commands) {
            if (command.contains("@p"))
                command = command.replace("@p", CommonReference.getName(playerMP));

            if (command.contains("@pX"))
                command = command.replace("@pX", String.valueOf((int) playerMP.posX));
            if (command.contains("@pY"))
                command = command.replace("@pY", String.valueOf((int) playerMP.posY));
            if (command.contains("@pZ"))
                command = command.replace("@pZ", String.valueOf((int) playerMP.posZ));
            if (command.contains("@dim"))
                command = command.replace("@dim", String.valueOf(playerMP.dimension));

            result = CommonReference.getServer().commandManager.executeCommand(CommonReference.getServer(), command);

            if (result == 0)
                OxygenMain.LOGGER.error("[Store] Failed to execute command for <{}/{}>: {}", 
                        CommonReference.getName(playerMP), 
                        CommonReference.getPersistentUUID(playerMP),
                        command);
        }
        return true;
    }
}
