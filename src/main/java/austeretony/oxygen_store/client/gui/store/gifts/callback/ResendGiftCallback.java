package austeretony.oxygen_store.client.gui.store.gifts.callback;

import java.util.UUID;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackBackgroundFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenKeyButton;
import austeretony.oxygen_core.client.gui.elements.OxygenTextBoxField;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.client.gui.elements.OxygenUsernameField;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.client.gui.store.GiftsSection;
import austeretony.oxygen_store.client.gui.store.StoreMenuScreen;
import austeretony.oxygen_store.common.store.gift.Gift;

public class ResendGiftCallback extends AbstractGUICallback {

    private final StoreMenuScreen screen;

    private final GiftsSection section;

    private OxygenUsernameField usernameField;

    private OxygenTextBoxField messageBoxField;

    private OxygenKeyButton confirmButton, cancelButton;

    //cache

    private boolean initialized;

    @Nullable
    private UUID selectedUUID;

    public ResendGiftCallback(StoreMenuScreen screen, GiftsSection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.enableDefaultBackground(EnumBaseGUISetting.FILL_CALLBACK_COLOR.get().asInt());
        this.addElement(new OxygenCallbackBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_store.gui.store.gifts.callback.resendGift"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(new OxygenTextLabel(6, 22, ClientReference.localize("oxygen_store.gui.store.callback.label.username"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(new OxygenTextLabel(6, 40, ClientReference.localize("oxygen_store.gui.store.callback.label.message"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(this.messageBoxField = new OxygenTextBoxField(6, 42, this.getWidth() - 12, 30, Gift.MAX_MESSAGE_LENGTH));

        this.addElement(this.confirmButton = new OxygenKeyButton(15, this.getHeight() - 10, ClientReference.localize("oxygen_core.gui.confirm"), Keyboard.KEY_R, this::confirm));
        this.addElement(this.cancelButton = new OxygenKeyButton(this.getWidth() - 55, this.getHeight() - 10, ClientReference.localize("oxygen_core.gui.cancel"), Keyboard.KEY_X, this::close));

        this.addElement(this.usernameField = new OxygenUsernameField(6, 24, this.getWidth() - 12));    
        this.usernameField.setUsernameSelectListener(
                (sharedData)->{
                    this.selectedUUID = sharedData.getPlayerUUID();
                    this.confirmButton.enable();
                });
    }

    @Override
    public void onOpen() {
        this.usernameField.reset();
        this.messageBoxField.reset();
        this.confirmButton.disable();

        if (!this.initialized) {
            this.initialized = true;
            this.usernameField.load();
        }
    }

    private void confirm() {
        if (!this.usernameField.isDragged()
                && !this.messageBoxField.isDragged()) {
            StoreManagerClient.instance().getStoreOperationsManager().resendGiftSynced(
                    this.section.getCurrentGiftEntry().getWrapped().getId(), 
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
            if (element == this.cancelButton)
                this.close();
            else if (element == this.confirmButton)
                this.confirm();
        }
    }
}
