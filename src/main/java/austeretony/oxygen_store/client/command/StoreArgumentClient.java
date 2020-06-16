package austeretony.oxygen_store.client.command;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.client.StoreMenuManager;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.main.EnumStorePrivilege;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class StoreArgumentClient implements ArgumentExecutor {

    @Override
    public String getName() {
        return "store";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            if (StoreConfig.ENABLE_STORE_ACCESS_CLIENTSIDE.asBoolean() 
                    && PrivilegesProviderClient.getAsBoolean(EnumStorePrivilege.STORE_ACCESS.id(), StoreConfig.ENABLE_STORE_ACCESS.asBoolean()))
                OxygenHelperClient.scheduleTask(StoreMenuManager::openStoreMenuDelegated, 100L, TimeUnit.MILLISECONDS);
        } else if (args.length == 2) {
            if (args[1].equals("-reset-data")) {
                StoreManagerClient.instance().getOffersContainer().reset();
                ClientReference.showChatMessage("oxygen_store.command.client.dataReset");
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, "-reset-data");
        return Collections.<String>emptyList();
    }
}
