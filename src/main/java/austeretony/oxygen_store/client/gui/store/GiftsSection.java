package austeretony.oxygen_store.client.gui.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenCurrencyValue;
import austeretony.oxygen_core.client.gui.elements.OxygenDefaultBackgroundUnderlinedFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenInventoryLoad;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenSectionSwitcher;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.OxygenTextField;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.client.gui.store.gifts.GiftPanelEntry;
import austeretony.oxygen_store.client.gui.store.gifts.callback.AcceptGiftCallback;
import austeretony.oxygen_store.client.gui.store.gifts.callback.ResendGiftCallback;
import austeretony.oxygen_store.client.gui.store.gifts.callback.ReturnGiftCallback;
import austeretony.oxygen_store.client.gui.store.gifts.context.AcceptGiftContextAction;
import austeretony.oxygen_store.client.gui.store.gifts.context.ResendGiftContextAction;
import austeretony.oxygen_store.client.gui.store.gifts.context.ReturnGiftContextAction;
import austeretony.oxygen_store.client.gui.store.gifts.context.ViewOfferContextAction;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.store.StoreOffer;
import austeretony.oxygen_store.common.store.gift.Gift;

public class GiftsSection extends AbstractGUISection {

    private final StoreMenuScreen screen;

    private OxygenTextField searchField;

    private OxygenSorter nameSorter, receiveDateSorter;

    private OxygenTextLabel giftsAmountLabel;

    private OxygenScrollablePanel giftsPanel;

    private OxygenInventoryLoad inventoryLoad;

    private OxygenCurrencyValue balanceValue;

    private AbstractGUICallback acceptGiftCallback, returnGiftCallback, resendGiftCallback;

    //cache

    private GiftPanelEntry currGiftEntry;

    public GiftsSection(StoreMenuScreen screen) {
        super(screen);
        this.screen = screen;
        this.setDisplayText(ClientReference.localize("oxygen_store.gui.store.section.gifts"));
    }

    @Override
    public void init() {
        this.acceptGiftCallback = new AcceptGiftCallback(this.screen, this, 140, 36);
        this.returnGiftCallback = new ReturnGiftCallback(this.screen, this, 140, 36);
        this.resendGiftCallback = new ResendGiftCallback(this.screen, this, 140, 84);

        this.addElement(new OxygenDefaultBackgroundUnderlinedFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_store.gui.store.title"), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.giftsAmountLabel = new OxygenTextLabel(0, 23, "", EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.nameSorter = new OxygenSorter(6, 29, EnumSorting.DOWN, ClientReference.localize("oxygen_store.gui.store.tooltip.offerName")));   

        this.nameSorter.setSortingListener((sorting)->{
            this.receiveDateSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortGifts(0);
            else
                this.sortGifts(1);
        });

        this.addElement(this.receiveDateSorter = new OxygenSorter(12, 29, EnumSorting.INACTIVE, ClientReference.localize("oxygen_store.gui.store.tooltip.receiveDate")));  

        this.receiveDateSorter.setSortingListener((sorting)->{
            this.nameSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortGifts(2);
            else
                this.sortGifts(3);
        }); 

        this.addElement(this.giftsPanel = new OxygenScrollablePanel(this.screen, 6, 37, this.getWidth() - 15, 16, 1, 90, 9, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));      
        this.addElement(this.searchField = new OxygenTextField(6, 16, 60, 24, ""));
        this.giftsPanel.initSearchField(this.searchField);

        this.giftsPanel.<GiftPanelEntry>setElementClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            this.currGiftEntry = clicked;
            if (!StoreManagerClient.instance().getPlayerDataContainer().isGiftChecked(clicked.getWrapped().getId())) {
                StoreManagerClient.instance().getPlayerDataContainer().markGiftChecked(clicked.getWrapped().getId());
                StoreManagerClient.instance().getPlayerDataContainer().setChanged(true);
                clicked.checked();
            }
        });

        this.giftsPanel.initContextMenu(new OxygenContextMenu(
                new ViewOfferContextAction(this),
                new AcceptGiftContextAction(this),
                new ReturnGiftContextAction(this),
                new ResendGiftContextAction(this)));

        this.addElement(new OxygenSectionSwitcher(this.getWidth() - 4, 5, this, this.screen.getOffersSection()));

        this.addElement(this.inventoryLoad = new OxygenInventoryLoad(6, this.getHeight() - 8));
        this.inventoryLoad.setLoad(this.screen.getOffersSection().getInventoryLoad().getLoad());
        this.addElement(this.balanceValue = new OxygenCurrencyValue(this.getWidth() - 14, this.getHeight() - 10));   
        this.balanceValue.setValue(StoreConfig.STORE_CURRENCY_INDEX.asInt(), this.screen.getOffersSection().getBalanceValue().getValue());
    }

    private void sortGifts(int mode) {
        List<Gift> gifts = new ArrayList<>(StoreManagerClient.instance().getPlayerDataContainer().getGifts());

        if (mode == 0)
            Collections.sort(gifts, (g1, g2)->getOfferName(g1).compareTo(getOfferName(g2)));
        else if (mode == 1)
            Collections.sort(gifts, (g1, g2)->getOfferName(g2).compareTo(getOfferName(g1)));
        else if (mode == 2)
            Collections.sort(gifts, (g1, g2)->g2.getId() < g1.getId() ? - 1 : g2.getId() > g1.getId() ? 1 : 0);
        else if (mode == 3)
            Collections.sort(gifts, (g1, g2)->g1.getId() < g2.getId() ? - 1 : g1.getId() > g2.getId() ? 1 : 0);

        this.giftsPanel.reset();
        for (Gift gift : gifts)
            this.giftsPanel.addEntry(new GiftPanelEntry(gift));

        this.searchField.reset();

        this.giftsAmountLabel.setDisplayText(String.valueOf(gifts.size()) + "/" + StoreConfig.GIFTS_INVENTORY_SIZE.asInt());
        this.giftsAmountLabel.setX(this.getWidth() - 6 - this.textWidth(this.giftsAmountLabel.getDisplayText(), this.giftsAmountLabel.getTextScale()));

        int maxRows = MathUtils.clamp(gifts.size(), 9, MathUtils.greaterOfTwo(90, StoreConfig.GIFTS_INVENTORY_SIZE.asInt()));
        this.giftsPanel.getScroller().reset();
        this.giftsPanel.getScroller().updateRowsAmount(maxRows);
    }

    private static String getOfferName(Gift gift) {
        StoreOffer offer = StoreManagerClient.instance().getOffersContainer().getOfferByPersistentId(gift.getOfferPersistentId());
        return offer != null ? offer.getName() : "";
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {}

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if (this.getCurrentCallback() == null 
                && !this.searchField.isDragged())
            if (OxygenGUIHelper.isOxygenMenuEnabled()) {
                if (keyCode == StoreMenuScreen.STORE_MENU_ENTRY.getKeyCode())
                    this.screen.close();
            } else if (StoreConfig.ENABLE_STORE_MENU_KEY.asBoolean() 
                    && keyCode == StoreManagerClient.instance().getKeyHandler().getStoreMenuKeybinding().getKeyCode())
                this.screen.close();
        return super.keyTyped(typedChar, keyCode); 
    }

    public void giftsSynchronized() {
        this.sortGifts(0);
    }

    public void giftRemoved(long id) {
        this.sortGifts(0);
    }

    public void viewOffer(StoreOffer offer) {
        this.screen.getOffersSection().open();
        this.screen.getOffersSection().viewOffer(offer);
    }

    public GiftPanelEntry getCurrentGiftEntry() {
        return this.currGiftEntry;
    }


    public void openAcceptGiftCallback() {
        this.acceptGiftCallback.open();
    }

    public void openReturnGiftCallback() {
        this.returnGiftCallback.open();
    }

    public void openResendGiftCallback() {
        this.resendGiftCallback.open();
    }
}
