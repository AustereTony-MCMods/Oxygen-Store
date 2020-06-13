package austeretony.oxygen_store.client.gui.settings;

import austeretony.alternateui.screen.framework.GUIElementsFramework;
import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenCheckBoxButton;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList.OxygenDropDownListWrapperEntry;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.client.gui.settings.ElementsContainer;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetColorCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetKeyCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetOffsetCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetScaleCallback;
import austeretony.oxygen_store.client.settings.EnumStoreClientSetting;
import austeretony.oxygen_store.client.settings.gui.EnumStoreGUISetting;

public class StoreSettingsContainer implements ElementsContainer {

    //common

    private OxygenCheckBoxButton addStoreMenuButton;

    //interface

    private OxygenDropDownList alignmentStoreMenu;


    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_store.gui.settings.module.store");
    }

    @Override
    public boolean hasCommonSettings() {
        return true;
    }

    @Override
    public boolean hasGUISettings() {
        return true;
    }

    @Override
    public void addCommon(GUIElementsFramework framework) {
        framework.addElement(new OxygenTextLabel(68, 25, ClientReference.localize("oxygen_core.gui.settings.option.oxygenMenu"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //add store menu to menu
        framework.addElement(new OxygenTextLabel(78, 34, ClientReference.localize("oxygen_store.gui.settings.option.addStoreMenu"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.addStoreMenuButton = new OxygenCheckBoxButton(68, 29));
        this.addStoreMenuButton.setToggled(EnumStoreClientSetting.ADD_STORE_MENU.get().asBoolean());
        this.addStoreMenuButton.setClickListener((mouseX, mouseY, mouseButton)->{
            EnumStoreClientSetting.ADD_STORE_MENU.get().setValue(String.valueOf(this.addStoreMenuButton.isToggled()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });
    }

    @Override
    public void addGUI(GUIElementsFramework framework) {
        framework.addElement(new OxygenTextLabel(68, 25, ClientReference.localize("oxygen_core.gui.settings.option.alignment"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //store menu alignment

        String currAlignmentStr;
        switch (EnumStoreGUISetting.STORE_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.left");
            break;
        case 0:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        case 1:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.right");
            break;    
        default:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        }
        framework.addElement(this.alignmentStoreMenu = new OxygenDropDownList(68, 35, 55, currAlignmentStr));
        this.alignmentStoreMenu.addElement(new OxygenDropDownListWrapperEntry<Integer>(- 1, ClientReference.localize("oxygen_core.alignment.left")));
        this.alignmentStoreMenu.addElement(new OxygenDropDownListWrapperEntry<Integer>(0, ClientReference.localize("oxygen_core.alignment.center")));
        this.alignmentStoreMenu.addElement(new OxygenDropDownListWrapperEntry<Integer>(1, ClientReference.localize("oxygen_core.alignment.right")));

        this.alignmentStoreMenu.<OxygenDropDownListWrapperEntry<Integer>>setElementClickListener((element)->{
            EnumStoreGUISetting.STORE_MENU_ALIGNMENT.get().setValue(String.valueOf(element.getWrapped()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });

        framework.addElement(new OxygenTextLabel(68, 33, ClientReference.localize("oxygen_store.gui.settings.option.alignmentStoreMenu"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));
    }

    @Override
    public void resetCommon() {
        //add store menu to menu
        this.addStoreMenuButton.setToggled(false);
        EnumStoreClientSetting.ADD_STORE_MENU.get().reset();    

        OxygenManagerClient.instance().getClientSettingManager().changed();
    }

    @Override
    public void resetGUI() {
        //store menu alignment
        this.alignmentStoreMenu.setDisplayText(ClientReference.localize("oxygen_core.alignment.center"));
        EnumStoreGUISetting.STORE_MENU_ALIGNMENT.get().reset();

        OxygenManagerClient.instance().getClientSettingManager().changed();
    }

    @Override
    public void initSetColorCallback(SetColorCallback callback) {}

    @Override
    public void initSetScaleCallback(SetScaleCallback callback) {}

    @Override
    public void initSetOffsetCallback(SetOffsetCallback callback) {}

    @Override
    public void initSetKeyCallback(SetKeyCallback callback) {}
}
