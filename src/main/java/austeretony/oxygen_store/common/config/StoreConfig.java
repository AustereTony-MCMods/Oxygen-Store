package austeretony.oxygen_store.common.config;

import java.util.List;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfig;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_core.common.config.ConfigValueUtils;
import austeretony.oxygen_store.common.main.StoreMain;

public class StoreConfig extends AbstractConfig {

    public static final ConfigValue
    ENABLE_STORE_MENU_KEY = ConfigValueUtils.getValue("client", "enable_store_menu_key", true),
    STORE_MENU_KEY = ConfigValueUtils.getValue("client", "store_menu_key", 53),

    STORE_CURRENCY_INDEX = ConfigValueUtils.getValue("server", "store_currency_index", 1, true),
    ENABLE_GIFTS = ConfigValueUtils.getValue("server", "enable_gifts", true, true),
    GIFTS_INVENTORY_SIZE = ConfigValueUtils.getValue("server", "gifts_inventory_size", 10, true),
    GIFT_EXPIRE_TIME_HOURS = ConfigValueUtils.getValue("server", "gift_expire_time_hours", 24, true),
    ENABLE_STORE_ACCESS = ConfigValueUtils.getValue("server", "enable_store_access", true, true),
    ENABLE_STORE_ACCESS_CLIENTSIDE = ConfigValueUtils.getValue("server", "enable_store_access_clientside", true, true),
    STORE_MENU_OPERATIONS_TIMEOUT_MILLIS = ConfigValueUtils.getValue("server", "store_menu_operations_timeout_millis", 240000),
    ENABLE_STORE_MANAGEMENT_INGAME = ConfigValueUtils.getValue("server", "enable_store_management_ingame", true),
    DEBUG_SCRIPTS = ConfigValueUtils.getValue("server", "debug_scripts", false),
    ADVANCED_LOGGING = ConfigValueUtils.getValue("server", "advanced_logging", false);

    @Override
    public String getDomain() {
        return StoreMain.MODID;
    }

    @Override
    public String getVersion() {
        return StoreMain.VERSION_CUSTOM;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/store.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(ENABLE_STORE_MENU_KEY);
        values.add(STORE_MENU_KEY);

        values.add(STORE_CURRENCY_INDEX);
        values.add(ENABLE_GIFTS);
        values.add(GIFTS_INVENTORY_SIZE);
        values.add(GIFT_EXPIRE_TIME_HOURS);
        values.add(ENABLE_STORE_ACCESS);
        values.add(ENABLE_STORE_ACCESS_CLIENTSIDE);
        values.add(STORE_MENU_OPERATIONS_TIMEOUT_MILLIS);
        values.add(ENABLE_STORE_MANAGEMENT_INGAME);
        values.add(DEBUG_SCRIPTS);
        values.add(ADVANCED_LOGGING);
    }
}
