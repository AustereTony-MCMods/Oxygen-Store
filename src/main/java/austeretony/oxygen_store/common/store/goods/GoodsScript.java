package austeretony.oxygen_store.common.store.goods;

import java.io.IOException;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.scripting.ScriptWrapper;
import austeretony.oxygen_core.common.scripting.ScriptingProvider;
import austeretony.oxygen_core.common.scripting.Shell;
import austeretony.oxygen_store.common.config.StoreConfig;
import net.minecraft.entity.player.EntityPlayerMP;

public class GoodsScript implements Goods {

    @Nullable
    private final ScriptWrapper scriptWrapper;

    private GoodsScript(@Nullable ScriptWrapper scriptWrapper) {
        this.scriptWrapper = scriptWrapper;
    }

    public static Goods fromJson(JsonElement element) {
        String pathStr = CommonReference.getGameFolder() + "/config/oxygen/data/server/store/offers/scripts/" + element.getAsString();
        ScriptWrapper scriptWrapper = null;
        try {
            scriptWrapper = ScriptWrapper.fromFile(
                    pathStr, 
                    element.getAsString());
        } catch (IOException exception) {
            OxygenMain.LOGGER.error("[Store] Failed to load offer script: {}", pathStr);
            exception.printStackTrace();
        }
        return new GoodsScript(scriptWrapper);
    }

    @Override
    public boolean collect(EntityPlayerMP playerMP) {
        if (this.scriptWrapper != null) {
            Shell shell = ScriptingProvider.createShell();

            shell.put("world", playerMP.world);//TODO It won't work outside of IDE, need some wrappers
            shell.put("player", playerMP);

            Object result = shell.evaluate(
                    this.scriptWrapper.getScriptText(), 
                    this.scriptWrapper.getName(), 
                    StoreConfig.DEBUG_SCRIPTS.asBoolean());

            return result != null && result instanceof Boolean && (Boolean) result;
        }
        return false;
    }
}
