package austeretony.oxygen_store.client.gui.store.offers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.alternateui.util.UIUtils;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.TimeHelperClient;
import austeretony.oxygen_core.client.api.WatcherHelperClient;
import austeretony.oxygen_core.client.currency.CurrencyProperties;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.client.gui.store.OffersSection;
import austeretony.oxygen_store.common.config.StoreConfig;
import austeretony.oxygen_store.common.store.OfferData;
import austeretony.oxygen_store.common.store.StoreOffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;

public class StoreOfferWidget extends GUISimpleElement<StoreOfferWidget> {

    private final StoreOffer offer;

    private final CurrencyProperties currencyProperties;

    private String priceStr, salePriceStr, purchasedStr, unavailableStr, saleStr;

    private boolean purchased, unavailable;

    private final List<String> description = new ArrayList<>(2);

    public StoreOfferWidget(int xPosition, int yPosition, StoreOffer offer, CurrencyProperties currencyProperties) {
        this.setPosition(xPosition, yPosition);
        this.setSize(offer.getWidgetSize().getWidth(), offer.getWidgetSize().getHeight());

        this.offer = offer;
        this.currencyProperties = currencyProperties;

        this.priceStr = offer.isFree() ? ClientReference.localize("oxygen_store.gui.store.label.free") : String.valueOf(offer.getPrice());
        this.salePriceStr = TextFormatting.STRIKETHROUGH + String.valueOf(offer.getPrice()) + TextFormatting.RESET 
                + " " + String.valueOf(offer.getSalePrice());
        this.saleStr = ClientReference.localize("oxygen_store.gui.store.label.sale");

        this.setEnabledTextColor(EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt());
        this.setDisabledTextColor(EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt());
        this.setStaticBackgroundColor(EnumBaseGUISetting.STATUS_TEXT_COLOR.get().asInt());
        this.setDebugColor(EnumBaseGUISetting.INACTIVE_TEXT_COLOR.get().asInt());
        this.setTextScale(EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat());
        this.requireDoubleClick();
        this.setVisible(true);

        UIUtils.divideText(this.description, offer.getDescription(), this.getWidth() - 8, this.getHeight() - 36, this.getTextScale() - 0.05F, 2);

        this.setEnabled(this.offer.getPrice() <= WatcherHelperClient.getLong(StoreConfig.STORE_CURRENCY_INDEX.asInt()));
    }

    void updateState(long balance) {
        this.setEnabled((this.offer.isSale() ? this.offer.getSalePrice() : this.offer.getPrice()) <= balance);

        OfferData offerData = StoreManagerClient.instance().getPlayerDataContainer().getOfferDataByOfferPersistentId(this.offer.getPersistentId());
        if (offerData != null) {
            if (this.offer.getMaxPurchases() != - 1 && offerData.getPurchasesAmount() >= this.offer.getMaxPurchases()) {
                this.purchased = true;
                this.purchasedStr = ClientReference.localize("oxygen_store.gui.store.label.purchased");
            }

            if (this.offer.getPurchasesCooldownSeconds() != 0 && TimeUnit.MILLISECONDS.toSeconds(TimeHelperClient.getServerZonedDateTime().toInstant().toEpochMilli() - offerData.getLastPurchaseTimeMillis()) < this.offer.getPurchasesCooldownSeconds()) {
                this.unavailable = true;
                this.unavailableStr = ClientReference.localize("oxygen_store.gui.store.label.unavailable");
                this.disable();
            }
        }
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.isVisible()) {   
            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getX(), this.getY(), 0.0F);           
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.enableBlend(); 
            this.mc.getTextureManager().bindTexture(this.offer.getWidgetTexture());
            GUIAdvancedElement.drawCustomSizedTexturedRect(0, 0, 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());                 
            GlStateManager.disableBlend();  

            if (this.isHovered())
                drawRect(0, 0, this.getWidth(), this.getHeight(), 0x20ffffff);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.pushMatrix();           
            GlStateManager.translate(2.0F, 2.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale() + 0.1F, this.getTextScale() + 0.1F, 0.0F);   
            this.mc.fontRenderer.drawString(this.offer.getName(), 0, 0, this.getEnabledTextColor(), true); 
            GlStateManager.popMatrix();

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.pushMatrix();           
            GlStateManager.translate(4.0F, 12.0F, 0.0F);           
            GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F);  
            int index = 0;
            for (String line : this.description) {
                this.mc.fontRenderer.drawString(line, 0.0F, (this.mc.fontRenderer.FONT_HEIGHT + 2.0F) * index, this.getDisabledTextColor(), true);
                index++;
            }
            GlStateManager.popMatrix();

            if (this.purchased) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                GlStateManager.pushMatrix();           
                GlStateManager.translate(2.0F, this.getHeight() - 8.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F);   
                this.mc.fontRenderer.drawString(this.purchasedStr, 0, 0, this.getStaticBackgroundColor(), true); 
                GlStateManager.popMatrix();
            } else if (this.offer.isSale()) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(2.0F, this.getHeight() - 8.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale() + 0.1F, this.getTextScale() + 0.1F, 0.0F);   
                this.mc.fontRenderer.drawString(this.saleStr, 0, 0, this.getStaticBackgroundColor(), true); 
                GlStateManager.popMatrix();
            }

            if (this.unavailable) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                GlStateManager.pushMatrix();           
                GlStateManager.translate(this.getWidth() - 4.0F - this.textWidth(this.unavailableStr, this.getTextScale() - 0.05F), this.getHeight() - 8.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F);   
                this.mc.fontRenderer.drawString(this.unavailableStr, 0, 0, this.getDebugColor(), true); 
                GlStateManager.popMatrix();
            } else {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                GlStateManager.pushMatrix();           
                GlStateManager.translate(this.getWidth() - 12.0F - this.textWidth(this.offer.isSale() ? this.salePriceStr : this.priceStr, this.getTextScale() - 0.05F), this.getHeight() - 8.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F); 
                this.mc.fontRenderer.drawString(this.offer.isSale() ? this.salePriceStr : this.priceStr, 0, 0, this.isEnabled() ? this.getEnabledTextColor() : this.getDebugColor(), true);
                GlStateManager.popMatrix(); 

                if (!offer.isFree()) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);  

                    GlStateManager.enableBlend(); 
                    this.mc.getTextureManager().bindTexture(this.currencyProperties.getIcon());
                    GUIAdvancedElement.drawCustomSizedTexturedRect(this.getWidth() - 10 + this.currencyProperties.getXOffset(), (this.getHeight() - 10) + this.currencyProperties.getYOffset(), 0, 0, this.currencyProperties.getIconWidth(), this.currencyProperties.getIconHeight(), this.currencyProperties.getIconWidth(), this.currencyProperties.getIconHeight());            
                    GlStateManager.disableBlend();
                }
            }

            if (this.offer.getIcons() != null)
                this.offer.getIcons().forEach(i->i.draw(this, mouseX, mouseY));

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (this.offer.getIcons() != null)
            this.offer.getIcons().forEach(i->i.drawTooltip(this, mouseX, mouseY));
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {       
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            OffersSection section = (OffersSection) this.getScreen().getWorkspace().getCurrentSection();
            section.setCurrentOfferWidget(this);
            section.openConfirmPurchaseCallback();
        }
        return false;
    }

    public StoreOffer getOffer() {
        return this.offer;
    }
}
