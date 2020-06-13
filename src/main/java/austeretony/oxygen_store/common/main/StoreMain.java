package austeretony.oxygen_store.common.main;

import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.gui.settings.SettingsScreen;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.privilege.PrivilegeUtils;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.PrivilegesProviderServer;
import austeretony.oxygen_core.server.command.CommandOxygenOperator;
import austeretony.oxygen_core.server.network.NetworkRequestsRegistryServer;
import austeretony.oxygen_core.server.timeout.TimeOutRegistryServer;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.client.StoreStatusMessagesHandler;
import austeretony.oxygen_store.client.command.StoreArgumentClient;
import austeretony.oxygen_store.client.event.StoreEventsClient;
import austeretony.oxygen_store.client.gui.settings.StoreSettingsContainer;
import austeretony.oxygen_store.client.gui.store.StoreMenuScreen;
import austeretony.oxygen_store.client.settings.EnumStoreClientSetting;
import austeretony.oxygen_store.client.settings.gui.EnumStoreGUISetting;
import austeretony.oxygen_store.client.sync.GiftsSyncHandlerClient;
import austeretony.oxygen_store.client.sync.OffersDataSyncHandlerClient;
import austeretony.oxygen_store.client.sync.OffersSyncHandlerClient;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.network.client.CPOpenStoreMenu;
import austeretony.oxygen_store.common.network.client.CPPurchaseSuccessful;
import austeretony.oxygen_store.common.network.client.CPRemoveGift;
import austeretony.oxygen_store.common.network.server.SPGiftOperation;
import austeretony.oxygen_store.common.network.server.SPStorePurchase;
import austeretony.oxygen_store.server.StoreManagerServer;
import austeretony.oxygen_store.server.command.StoreArgumentOperator;
import austeretony.oxygen_store.server.event.StoreEventsServer;
import austeretony.oxygen_store.server.sync.GiftsSyncHandlerServer;
import austeretony.oxygen_store.server.sync.OffersDataSyncHandlerServer;
import austeretony.oxygen_store.server.sync.OffersSyncHandlerServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = StoreMain.MODID, 
        name = StoreMain.NAME, 
        version = StoreMain.VERSION,
        dependencies = "required-after:oxygen_core@[0.11.3,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = StoreMain.VERSIONS_FORGE_URL)
public class StoreMain {

    public static final String 
    MODID = "oxygen_store",
    NAME = "Oxygen: Store",
    VERSION = "0.11.0",
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Store/info/mod_versions_forge.json";

    public static final int 
    STORE_MOD_INDEX = 17,

    STORE_SCREEN_ID = 170,

    STORE_OFFERS_DATA_ID = 170,
    STORE_OFFERS_INFO_DATA_ID = 171,
    STORE_GIFTS_DATA_ID = 172,

    STORE_NOTIFICATION_ID = 170,

    STORE_OPERATION_REQUEST_ID = 170,

    STORE_TIMEOUT_ID = 170;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new StoreConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgument(new StoreArgumentClient());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        StoreManagerServer.create();
        CommonReference.registerEvent(new StoreEventsServer());
        OxygenHelperServer.registerDataSyncHandler(new OffersSyncHandlerServer());
        OxygenHelperServer.registerDataSyncHandler(new OffersDataSyncHandlerServer());
        OxygenHelperServer.registerDataSyncHandler(new GiftsSyncHandlerServer());
        NetworkRequestsRegistryServer.registerRequest(STORE_OPERATION_REQUEST_ID, 1000);
        TimeOutRegistryServer.registerTimeOut(STORE_TIMEOUT_ID, StoreConfig.STORE_MENU_OPERATIONS_TIMEOUT_MILLIS.asInt());
        CommandOxygenOperator.registerArgument(new StoreArgumentOperator());
        EnumStorePrivilege.register();
        if (event.getSide() == Side.CLIENT) {
            StoreManagerClient.create();
            CommonReference.registerEvent(new StoreEventsClient());
            OxygenGUIHelper.registerOxygenMenuEntry(StoreMenuScreen.STORE_MENU_ENTRY);
            OxygenHelperClient.registerStatusMessagesHandler(new StoreStatusMessagesHandler());
            OxygenHelperClient.registerDataSyncHandler(new OffersSyncHandlerClient());
            OxygenHelperClient.registerDataSyncHandler(new OffersDataSyncHandlerClient());
            OxygenHelperClient.registerDataSyncHandler(new GiftsSyncHandlerClient());
            EnumStoreClientSetting.register();
            EnumStoreGUISetting.register();
            SettingsScreen.registerSettingsContainer(new StoreSettingsContainer());
        }
    }

    public static void addDefaultPrivileges() {
        if (PrivilegesProviderServer.getRole(OxygenMain.OPERATOR_ROLE_ID).getPrivilege(EnumStorePrivilege.STORE_ACCESS.id()) == null) {
            PrivilegesProviderServer.getRole(OxygenMain.OPERATOR_ROLE_ID).addPrivileges(
                    PrivilegeUtils.getPrivilege(EnumStorePrivilege.STORE_ACCESS.id(), true));
            OxygenManagerServer.instance().getPrivilegesContainer().markChanged();
            OxygenMain.LOGGER.info("[Store] Default Operator role privileges added.");
        }
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPOpenStoreMenu.class);
        OxygenMain.network().registerPacket(CPPurchaseSuccessful.class);
        OxygenMain.network().registerPacket(CPRemoveGift.class);

        OxygenMain.network().registerPacket(SPStorePurchase.class);
        OxygenMain.network().registerPacket(SPGiftOperation.class);
    }
}
