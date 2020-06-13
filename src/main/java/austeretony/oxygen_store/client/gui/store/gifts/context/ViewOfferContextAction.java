package austeretony.oxygen_store.client.gui.store.gifts.context;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_store.client.gui.store.GiftsSection;
import austeretony.oxygen_store.client.gui.store.gifts.GiftPanelEntry;

public class ViewOfferContextAction implements OxygenContextMenuAction {


    private final GiftsSection section;

    public ViewOfferContextAction(GiftsSection section) {
        this.section = section;
    }
    
    @Override
    public String getLocalizedName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_store.gui.store.context.viewOffer");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        return true;
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        GiftPanelEntry entry = (GiftPanelEntry) currElement; 
        this.section.viewOffer(entry.offer);
    }
}
