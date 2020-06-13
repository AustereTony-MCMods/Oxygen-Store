package austeretony.oxygen_store.client.gui.store;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.currency.CurrencyProperties;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_store.client.gui.menu.StoreMenuEntry;
import austeretony.oxygen_store.client.settings.gui.EnumStoreGUISetting;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.main.StoreMain;

public class StoreMenuScreen extends AbstractGUIScreen {

    public static final StoreMenuEntry STORE_MENU_ENTRY = new StoreMenuEntry();

    private CurrencyProperties currencyProperties;

    private OffersSection offersSection;

    private GiftsSection giftsSection;

    public StoreMenuScreen() {
        OxygenHelperClient.syncData(StoreMain.STORE_OFFERS_DATA_ID);
        OxygenHelperClient.syncData(StoreMain.STORE_OFFERS_INFO_DATA_ID);
        if (StoreConfig.ENABLE_GIFTS.asBoolean())
            OxygenHelperClient.syncData(StoreMain.STORE_GIFTS_DATA_ID);

        this.currencyProperties = OxygenHelperClient.getCurrencyProperties(StoreConfig.STORE_CURRENCY_INDEX.asInt());
        if (this.currencyProperties == null)
            this.currencyProperties = OxygenHelperClient.getCurrencyProperties(OxygenMain.COMMON_CURRENCY_INDEX);
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        EnumGUIAlignment alignment = EnumGUIAlignment.CENTER;
        switch (EnumStoreGUISetting.STORE_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            alignment = EnumGUIAlignment.LEFT;
            break;
        case 0:
            alignment = EnumGUIAlignment.CENTER;
            break;
        case 1:
            alignment = EnumGUIAlignment.RIGHT;
            break;    
        default:
            alignment = EnumGUIAlignment.CENTER;
            break;
        }
        return new GUIWorkspace(this, 267, 204).setAlignment(alignment, 0, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.offersSection = (OffersSection) new OffersSection(this).enable());
        this.getWorkspace().initSection(this.giftsSection = (GiftsSection) new GiftsSection(this).setEnabled(StoreConfig.ENABLE_GIFTS.asBoolean()));
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        return this.offersSection;
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element) {}

    @Override
    protected boolean doesGUIPauseGame() {
        return false;
    }

    public void offersSynchronized() {
        this.offersSection.offersSynchronized();
    }

    public void offersDataSynchronized() {
        this.offersSection.offersDataSynchronized();
    }

    public void giftsSynchronized() {
        this.giftsSection.giftsSynchronized();
    }

    public void purchaseSuccessful(long persistentOfferId, long balance) {
        this.offersSection.purchaseSuccessful(persistentOfferId, balance);
    }

    public void giftRemoved(long id) {
        this.giftsSection.giftRemoved(id);
    }

    public CurrencyProperties getCurrencyProperties() {
        return this.currencyProperties;
    }

    public OffersSection getOffersSection() {
        return this.offersSection;
    }

    public GiftsSection getGiftsSection() {
        return this.giftsSection;
    }
}
