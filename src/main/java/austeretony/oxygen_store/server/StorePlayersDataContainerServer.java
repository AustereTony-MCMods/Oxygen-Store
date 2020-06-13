package austeretony.oxygen_store.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;

public class StorePlayersDataContainerServer extends AbstractPersistentData {

    private final Map<UUID, StorePlayerData> playersData = new ConcurrentHashMap<>();

    protected StorePlayersDataContainerServer() {}

    public Collection<StorePlayerData> getPlayersData() {
        return this.playersData.values();
    }

    @Nullable
    public StorePlayerData getPlayerData(UUID playerUUID) {
        return this.playersData.get(playerUUID);
    }

    public StorePlayerData createPlayerData(UUID playerUUID) {
        StorePlayerData playerData = new StorePlayerData(playerUUID);
        this.playersData.put(playerUUID, playerData);
        return playerData;
    }

    @Override
    public String getDisplayName() {
        return "store:players_data_server";
    }

    @Override
    public String getPath() {
        return OxygenHelperServer.getDataFolder() + "/server/world/store/players_data_server.dat";
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {  
        StreamUtils.write(this.playersData.size(), bos);
        for (StorePlayerData playerData : this.playersData.values())
            playerData.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        int amount = StreamUtils.readInt(bis);
        StorePlayerData playerData;
        for (int i = 0; i < amount; i++) {
            playerData = StorePlayerData.read(bis);
            this.playersData.put(playerData.getPlayerUUID(), playerData);
        }
        StoreManagerServer.instance().getStoreOperationsManager().processExpiredGifts();
        OxygenMain.LOGGER.info("[Store] Loaded {} players data entries.", amount);
    }

    @Override
    public void reset() {
        this.playersData.clear();
    }
}
