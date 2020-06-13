package austeretony.oxygen_store.client.gui.store.offers;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.oxygen_core.client.currency.CurrencyProperties;
import austeretony.oxygen_store.common.store.StoreOffer;
import net.minecraft.client.renderer.GlStateManager;

public class StoreWidgetContainerPanelEntry extends GUIButton {

    private final StoreOfferWidget[] widgets;

    public StoreWidgetContainerPanelEntry(CurrencyProperties currencyProperties, StoreOffer... offers) {
        this.widgets = new StoreOfferWidget[offers.length];

        int index = 0;
        for (StoreOffer offer : offers)
            this.widgets[index] = new StoreOfferWidget((offer.getWidgetSize().getWidth() + 1) * index + index++, 0, offer, currencyProperties);
    }

    @Override
    public void init() {
        for (StoreOfferWidget widget : this.widgets)
            widget.initScreen(this.getScreen());
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();           
        GlStateManager.translate(this.getX(), this.getY(), 0.0F);            

        for (StoreOfferWidget widget : this.widgets)
            widget.draw(mouseX - this.getX(), mouseY - this.getY());

        GlStateManager.popMatrix();
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {        
        for (StoreOfferWidget widget : this.widgets)
            widget.drawTooltip(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {   
        for (StoreOfferWidget widget : this.widgets)
            widget.mouseClicked(mouseX, mouseY, mouseButton);
        return false;
    }

    @Override
    public void mouseOver(int mouseX, int mouseY) {
        for (StoreOfferWidget widget : this.widgets)
            widget.mouseOver(mouseX - this.getX(), mouseY - this.getY());
    }

    public void updateWidgetsState(long balance) {
        for (StoreOfferWidget widget : this.widgets)
            widget.updateState(balance);
    }
}
