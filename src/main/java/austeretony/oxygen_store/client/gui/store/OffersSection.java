package austeretony.oxygen_store.client.gui.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.WatcherHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenCurrencyValue;
import austeretony.oxygen_core.client.gui.elements.OxygenDefaultBackgroundUnderlinedFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenInventoryLoad;
import austeretony.oxygen_core.client.gui.elements.OxygenPanelEntry;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenSectionSwitcher;
import austeretony.oxygen_core.client.gui.elements.OxygenTextField;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.client.gui.store.offers.StoreOfferWidget;
import austeretony.oxygen_store.client.gui.store.offers.StoreWidgetContainerPanelEntry;
import austeretony.oxygen_store.client.gui.store.offers.callback.ConfirmPurchaseCallback;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.store.EnumWidgetSize;
import austeretony.oxygen_store.common.store.StoreOffer;

public class OffersSection extends AbstractGUISection {

    private final StoreMenuScreen screen;

    private OxygenScrollablePanel categoriesPanel, widgetContainersPanel;

    private OxygenTextField searchField;

    private OxygenInventoryLoad inventoryLoad;

    private OxygenCurrencyValue balanceValue;

    private AbstractGUICallback confirmPurchaseCallback;

    //cache

    private boolean offersSynced, offersDataSynced;

    private final Multimap<String, StoreOffer> offersByCategories = HashMultimap.<String, StoreOffer>create();

    @Nullable
    private StoreOfferWidget currOffer;

    public OffersSection(StoreMenuScreen screen) {
        super(screen);
        this.screen = screen;
        this.setDisplayText(ClientReference.localize("oxygen_store.gui.store.section.offers"));
    }

    @Override
    public void init() {
        this.confirmPurchaseCallback = new ConfirmPurchaseCallback(this.screen, this, 140, 105);

        this.addElement(new OxygenDefaultBackgroundUnderlinedFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_store.gui.store.title"), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.searchField = new OxygenTextField(6, 16, 70, 24, ""));
        this.searchField.setInputListener((keyChar, keyCode)->this.updateSearchResult());

        this.addElement(this.categoriesPanel = new OxygenScrollablePanel(this.screen, 6, 26, 78, 10, 1, 150, 15, EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), true));

        this.categoriesPanel.<OxygenPanelEntry<String>>setElementClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (previous != clicked) {
                if (previous != null)
                    previous.setToggled(false);
                clicked.toggle();                    
                this.loadCategory(clicked.getWrapped());
            }
        });

        this.addElement(this.widgetContainersPanel = new OxygenScrollablePanel(this.screen, 88, 17, EnumWidgetSize.BIG.getWidth(), EnumWidgetSize.BIG.getHeight(), 2, 30, 3, EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), true));

        this.addElement(new OxygenSectionSwitcher(this.getWidth() - 4, 5, this, this.screen.getGiftsSection()));

        this.addElement(this.inventoryLoad = new OxygenInventoryLoad(6, this.getHeight() - 8));
        this.inventoryLoad.updateLoad();
        this.addElement(this.balanceValue = new OxygenCurrencyValue(this.getWidth() - 14, this.getHeight() - 10));   
        this.balanceValue.setValue(StoreConfig.STORE_CURRENCY_INDEX.asInt(), WatcherHelperClient.getLong(StoreConfig.STORE_CURRENCY_INDEX.asInt()));
    }

    private void loadCategory(String category) {
        List<StoreOffer> sortedOffers = this.offersByCategories.get(category)
                .stream()
                .sorted((o1, o2)->o1.getPosition() - o2.getPosition())
                .collect(Collectors.toList());
        this.loadOffers(sortedOffers, false);
        this.updateWidgetsState(this.balanceValue.getValue());
    }

    private void updateSearchResult() {
        String typedText = this.searchField.getTypedText();
        if (!typedText.isEmpty()) {
            List<StoreOffer> offers = StoreManagerClient.instance().getOffersContainer().getOffers()
                    .stream()
                    .filter((offer)->offer.getName().startsWith(typedText) || offer.getName().contains(" " + typedText))
                    .collect(Collectors.toList());
            this.loadOffers(offers, false);
            this.updateWidgetsState(this.balanceValue.getValue());
        } else {
            this.categoriesPanel.reset();
            this.initCategories();
        }
    }

    private void loadOffers(List<StoreOffer> offers, boolean ignoreUnavailability) {
        this.widgetContainersPanel.reset();

        Iterator<StoreOffer> iterator = offers.iterator();
        StoreOffer current;
        final int widgetsPerContainer = 2;
        int containerFill = 0;

        StoreOffer[] containedOffers = null;
        StoreWidgetContainerPanelEntry containerEntry = null;
        boolean containerFilled = false;
        for (int i = 0; i < offers.size(); i++) {
            if (iterator.hasNext()) {
                current = iterator.next();
                if (!current.isAvailable()) continue;
                if (current.getWidgetSize() == EnumWidgetSize.SMALL) {
                    if (containedOffers == null)
                        containedOffers = new StoreOffer[widgetsPerContainer];
                    containedOffers[containerFill++] = current;
                    if (containerFill == widgetsPerContainer)
                        containerFilled = true;
                } else if (current.getWidgetSize() == EnumWidgetSize.BIG) {
                    if (containerFill > 0) {
                        this.widgetContainersPanel.addEntry(new StoreWidgetContainerPanelEntry(
                                this.screen.getCurrencyProperties(), 
                                (StoreOffer[]) Arrays.copyOf(containedOffers, containerFill)));
                        containerFill = 0;
                    }

                    containedOffers = new StoreOffer[] {current};
                    containerFilled = true;
                }
                if (containerFilled) {
                    this.widgetContainersPanel.addEntry(new StoreWidgetContainerPanelEntry(
                            this.screen.getCurrencyProperties(), 
                            containedOffers));

                    containedOffers = null;
                    containerFill = 0;
                    containerFilled = false;
                } else if (!iterator.hasNext())
                    this.widgetContainersPanel.addEntry(new StoreWidgetContainerPanelEntry(
                            this.screen.getCurrencyProperties(), 
                            (StoreOffer[]) Arrays.copyOf(containedOffers, containerFill)));
            } else
                break;
        }

        this.widgetContainersPanel.getScroller().reset();
        this.widgetContainersPanel.getScroller().updateRowsAmount(
                MathUtils.clamp(this.widgetContainersPanel.buttonsBuffer.size(), 3, MathUtils.greaterOfTwo(this.widgetContainersPanel.buttonsBuffer.size(), 3)));

    }

    private void initCategories() {
        TreeSet<String> sortedCategories = new TreeSet<>(this.offersByCategories.keySet());

        OxygenPanelEntry entry;
        for (String category : sortedCategories) {
            entry = new OxygenPanelEntry(category, category, false);
            entry.setEnabledTextColor(EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt());
            this.categoriesPanel.addEntry(entry);
        }

        this.categoriesPanel.getScroller().updateRowsAmount(
                MathUtils.clamp(sortedCategories.size(), 15, MathUtils.greaterOfTwo(sortedCategories.size(), 15)));

        if (!sortedCategories.isEmpty()) {
            this.loadCategory(sortedCategories.first());

            GUIButton first = this.categoriesPanel.buttonsBuffer.get(0);
            first.toggle();
            this.categoriesPanel.setPreviousClickedButton(first);
        }
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {}

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if (!this.searchField.isDragged())
            if (OxygenGUIHelper.isOxygenMenuEnabled()) {
                if (keyCode == StoreMenuScreen.STORE_MENU_ENTRY.getKeyCode())
                    this.screen.close();
            } else if (StoreConfig.ENABLE_STORE_MENU_KEY.asBoolean() 
                    && keyCode == StoreManagerClient.instance().getKeyHandler().getStoreMenuKeybinding().getKeyCode())
                this.screen.close();
        return super.keyTyped(typedChar, keyCode); 
    }

    public void offersSynchronized() {
        for (StoreOffer offer : StoreManagerClient.instance().getOffersContainer().getOffers()) 
            this.offersByCategories.put(ClientReference.localize(offer.getCategory()), offer);
        this.initCategories();

        this.offersSynced = true;
        if (this.offersDataSynced)
            this.updateWidgetsState(this.balanceValue.getValue());
    }

    public void offersDataSynchronized() {
        this.offersDataSynced = true;
        if (this.offersSynced)
            this.updateWidgetsState(this.balanceValue.getValue());
    }

    private void updateWidgetsState(long balance) {
        for (GUIButton button : this.widgetContainersPanel.buttonsBuffer)
            ((StoreWidgetContainerPanelEntry) button).updateWidgetsState(balance);
    }

    public void purchaseSuccessful(long persistentOfferId, long balance) {
        this.balanceValue.setValue(StoreConfig.STORE_CURRENCY_INDEX.asInt(), balance);
        this.updateWidgetsState(balance);
    }

    public void setCurrentOfferWidget(StoreOfferWidget widget) {
        this.currOffer = widget;
    }

    public void viewOffer(StoreOffer offer) {
        List<StoreOffer> offers = new ArrayList<>(1);
        offers.add(offer);
        this.loadOffers(offers, true);
        this.updateWidgetsState(this.balanceValue.getValue());
    }

    @Nullable
    public StoreOfferWidget getCurrentOfferWidget() {
        return this.currOffer;
    }

    public OxygenInventoryLoad getInventoryLoad() {
        return this.inventoryLoad;
    }

    public OxygenCurrencyValue getBalanceValue() {
        return this.balanceValue;
    }

    public void openConfirmPurchaseCallback() {
        this.confirmPurchaseCallback.open();
    }
}
