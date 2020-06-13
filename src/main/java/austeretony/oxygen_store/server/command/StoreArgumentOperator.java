package austeretony.oxygen_store.server.command;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_store.common.main.StoreMain;
import austeretony.oxygen_store.common.network.client.CPOpenStoreMenu;
import austeretony.oxygen_store.server.StoreManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class StoreArgumentOperator implements ArgumentExecutor {

    @Override
    public String getName() {
        return "store";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP playerMP = null, targetPlayerMP;
        if (sender instanceof EntityPlayerMP)
            playerMP = CommandBase.getCommandSenderAsPlayer(sender);

        if (args.length >= 2) {
            if (args[1].equals("-open-menu")) {
                if (args.length == 3) {
                    targetPlayerMP = CommandBase.getPlayer(server, sender, args[2]);
                    OxygenHelperServer.resetTimeOut(CommonReference.getPersistentUUID(targetPlayerMP), StoreMain.STORE_TIMEOUT_ID);
                    OxygenMain.network().sendTo(new CPOpenStoreMenu(), targetPlayerMP);
                }
            } else if (args[1].equals("-reload-offers"))
                StoreManagerServer.instance().getStoreOperationsManager().reloadOffers(playerMP);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, "-open-menu", "-reload-offers");
        return Collections.<String>emptyList();
    }
}
