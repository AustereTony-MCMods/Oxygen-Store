package austeretony.oxygen_store.client.gui.store.offers.callback;

import java.util.UUID;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackBackgroundFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenCheckBoxButton;
import austeretony.oxygen_core.client.gui.elements.OxygenKeyButton;
import austeretony.oxygen_core.client.gui.elements.OxygenTextBoxField;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.client.gui.elements.OxygenUsernameField;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.client.gui.store.OffersSection;
import austeretony.oxygen_store.client.gui.store.StoreMenuScreen;
import austeretony.oxygen_store.common.store.gift.Gift;

public class ConfirmPurchaseCallback extends AbstractGUICallback {

    private final StoreMenuScreen screen;

    private final OffersSection section;

    private OxygenCheckBoxButton selfButton, giftButton;

    private OxygenUsernameField usernameField;

    private OxygenTextBoxField messageBoxField;

    private OxygenKeyButton confirmButton, cancelButton;

    //cache

    private boolean initialized;

    @Nullable
    private UUID selectedUUID;

    public ConfirmPurchaseCallback(StoreMenuScreen screen, OffersSection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.enableDefaultBackground(EnumBaseGUISetting.FILL_CALLBACK_COLOR.get().asInt());
        this.addElement(new OxygenCallbackBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_store.gui.store.callback.confirmPurchase"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.selfButton = new OxygenCheckBoxButton(6, 18)); 
        this.addElement(new OxygenTextLabel(14, 24, ClientReference.localize("oxygen_store.gui.store.callback.label.self"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.giftButton = new OxygenCheckBoxButton(6, 28)); 
        this.addElement(new OxygenTextLabel(14, 34, ClientReference.localize("oxygen_store.gui.store.callback.label.gift"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(new OxygenTextLabel(6, 43, ClientReference.localize("oxygen_store.gui.store.callback.label.username"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(new OxygenTextLabel(6, 61, ClientReference.localize("oxygen_store.gui.store.callback.label.message"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(this.messageBoxField = new OxygenTextBoxField(6, 63, this.getWidth() - 12, 30, Gift.MAX_MESSAGE_LENGTH));

        this.addElement(this.confirmButton = new OxygenKeyButton(15, this.getHeight() - 10, ClientReference.localize("oxygen_core.gui.confirm"), Keyboard.KEY_R, this::confirm));
        this.addElement(this.cancelButton = new OxygenKeyButton(this.getWidth() - 55, this.getHeight() - 10, ClientReference.localize("oxygen_core.gui.cancel"), Keyboard.KEY_X, this::close));

        this.addElement(this.usernameField = new OxygenUsernameField(6, 45, this.getWidth() - 12));    
        this.usernameField.setUsernameSelectListener(
                (sharedData)->{
                    this.selectedUUID = sharedData.getPlayerUUID();
                    this.confirmButton.enable();
                });
    }

    @Override
    public void onOpen() {
        this.selfButton.enable();
        this.selfButton.toggle();
        this.giftButton.setEnabled(this.section.getCurrentOfferWidget().getOffer().canBeGifted());
        this.giftButton.setToggled(false);

        this.usernameField.reset();
        this.usernameField.disable();
        this.messageBoxField.reset();
        this.messageBoxField.disable();
        this.confirmButton.enable();

        if (!this.initialized) {
            this.initialized = true;
            this.usernameField.load();
        }
    }

    private void confirm() {
        if (!this.usernameField.isDragged()
                && !this.messageBoxField.isDragged()) {
            if (this.selfButton.isToggled())
                StoreManagerClient.instance().getStoreOperationsManager().purchaseSelfSynced(this.section.getCurrentOfferWidget().getOffer().getPersistentId());
            else if (this.giftButton.isToggled() && this.selectedUUID != null)
                StoreManagerClient.instance().getStoreOperationsManager().purchaseGiftSynced(
                        this.section.getCurrentOfferWidget().getOffer().getPersistentId(), 
                        this.selectedUUID, 
                        this.messageBoxField.getTypedText());
            this.close();
        }
    }

    @Override
    public void close() {
        if (!this.usernameField.isDragged()
                && !this.messageBoxField.isDragged())
            super.close();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.selfButton) {
                if (this.selfButton.isToggled()) {
                    this.giftButton.setToggled(false);
                    this.usernameField.disable();
                    this.messageBoxField.disable();
                    this.confirmButton.enable();
                }
            } else if (element == this.giftButton) {
                if (this.giftButton.isToggled()) {
                    this.selfButton.setToggled(false);
                    this.usernameField.enable();
                    this.messageBoxField.enable();
                    this.confirmButton.disable();
                }
            } else if (element == this.cancelButton)
                this.close();
            else if (element == this.confirmButton)
                this.confirm();
        }
    }
}
