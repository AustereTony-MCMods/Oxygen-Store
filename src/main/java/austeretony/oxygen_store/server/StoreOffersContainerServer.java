package austeretony.oxygen_store.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_store.common.store.StoreOffer;

public class StoreOffersContainerServer {

    private final Map<Long, StoreOffer> offers = new ConcurrentHashMap<>();

    protected StoreOffersContainerServer() {}

    public Set<Long> getOffersIds() {
        return this.offers.keySet();
    }

    @Nullable
    public StoreOffer getOffer(long versionId) {
        return this.offers.get(versionId);
    }

    @Nullable
    public StoreOffer getOfferByPersistentId(long persistentId) {
        for (StoreOffer offer : this.offers.values())
            if (offer.getPersistentId() == persistentId)
                return offer;
        return null;
    }

    public Future<?> loadAsync() {
        return OxygenHelperServer.addIOTask(this::load);
    }

    private void load() {
        this.offers.clear();

        String folder = OxygenHelperCommon.getConfigFolder() + "data/server/store/offers";
        File file = new File(folder);
        if (file.exists())
            this.loadOffersFromFolder(file);
        OxygenMain.LOGGER.info("[Store] Loaded {} store offers.", this.offers.size());
    }

    private void loadOffersFromFolder(File folder) {
        for (File entry : folder.listFiles()) {
            if (entry.isDirectory())
                this.loadOffersFromFolder(entry);
            else
                if (entry.getName().endsWith(".json"))
                    this.loadOffers(entry);
        }
    }

    private void loadOffers(File file) {
        try (
                InputStream inputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")) {       
            JsonArray offersArray = new JsonParser().parse(reader).getAsJsonArray();
            StoreOffer offer;
            int index = 0;
            for (JsonElement offerElement : offersArray) {
                offer = StoreOffer.fromJson(offerElement.getAsJsonObject());
                offer.setPosition(index++);
                this.offers.put(offer.getId(), offer);
            }
        } catch (IOException exception) {
            OxygenMain.LOGGER.error("[Store] Failed to load offer(s) from file: {}", file.getName());
            exception.printStackTrace();
        }
    }
}
