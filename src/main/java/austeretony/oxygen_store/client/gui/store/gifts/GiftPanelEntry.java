package austeretony.oxygen_store.client.gui.store.gifts;

import java.util.ArrayList;
import java.util.List;

import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.alternateui.util.UIUtils;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.TimeHelperClient;
import austeretony.oxygen_core.client.gui.OxygenGUITextures;
import austeretony.oxygen_core.client.gui.OxygenGUIUtils;
import austeretony.oxygen_core.client.gui.elements.OxygenWrapperPanelEntry;
import austeretony.oxygen_store.client.StoreManagerClient;
import austeretony.oxygen_store.common.store.StoreOffer;
import austeretony.oxygen_store.common.store.gift.EnumGiftType;
import austeretony.oxygen_store.common.store.gift.Gift;
import net.minecraft.client.renderer.GlStateManager;

public class GiftPanelEntry extends OxygenWrapperPanelEntry<Gift> {

    private final int tooltipBackgroundColor, tooltipFrameColor;

    public final StoreOffer offer;

    private final String senderName, giftTypeStr, returnedByStr, receiveDateStr;

    private List<String> message = new ArrayList<>();

    public GiftPanelEntry(Gift gift) {
        super(gift);

        this.offer = StoreManagerClient.instance().getOffersContainer().getOfferByPersistentId(gift.getOfferPersistentId());

        this.senderName = ClientReference.localize("oxygen_store.gui.store.label.from", gift.getSenderUsername());
        this.giftTypeStr = gift.getType().localized();
        this.returnedByStr = ClientReference.localize("oxygen_store.gui.store.label.from", gift.getReceiverUsername());
        this.receiveDateStr = TimeHelperClient.getDateTimeFormatter().format(TimeHelperClient.getServerZonedDateTime(gift.getId()));

        this.tooltipBackgroundColor = EnumBaseGUISetting.BACKGROUND_BASE_COLOR.get().asInt();
        this.tooltipFrameColor = EnumBaseGUISetting.BACKGROUND_ADDITIONAL_COLOR.get().asInt();
        this.setTooltipScaleFactor(EnumBaseGUISetting.TEXT_TOOLTIP_SCALE.get().asFloat());
        this.setStaticBackgroundColor(EnumBaseGUISetting.STATUS_TEXT_COLOR.get().asInt());
        this.setDynamicBackgroundColor(EnumBaseGUISetting.ELEMENT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.ELEMENT_DISABLED_COLOR.get().asInt(), EnumBaseGUISetting.ELEMENT_HOVERED_COLOR.get().asInt());
        this.setTextDynamicColor(EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_DISABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_HOVERED_COLOR.get().asInt());
        this.setDisplayText(this.offer.getName());

        if (!gift.getMessage().isEmpty())
            UIUtils.divideText(this.message, gift.getMessage(), 128, 30, EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), 2);

        if (StoreManagerClient.instance().getPlayerDataContainer().isGiftChecked(gift.getId()))
            this.checked();
    }

    public void checked() {
        this.setTextDynamicColor(EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_DARK_DISABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_DARK_HOVERED_COLOR.get().asInt());
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();           
        GlStateManager.translate(this.getX(), this.getY(), 0.0F);    
        GlStateManager.scale(this.getScale(), this.getScale(), 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);  

        int 
        color = this.getEnabledBackgroundColor(), 
        textColor = this.getEnabledTextColor(),
        noteIconU = 0;

        if (!this.isEnabled()) {                 
            color = this.getDisabledBackgroundColor();
            textColor = this.getDisabledTextColor();  
            noteIconU = 6;
        } else if (this.isHovered()) {                 
            color = this.getHoveredBackgroundColor();
            textColor = this.getHoveredTextColor();
            noteIconU = 12;
        }

        int third = this.getWidth() / 3;
        OxygenGUIUtils.drawGradientRect(0.0D, 0.0D, third, this.getHeight(), 0x00000000, color, EnumGUIAlignment.RIGHT);
        drawRect(third, 0, this.getWidth() - third, this.getHeight(), color);
        OxygenGUIUtils.drawGradientRect(this.getWidth() - third, 0.0D, this.getWidth(), this.getHeight(), 0x00000000, color, EnumGUIAlignment.LEFT);

        float textScale = this.getTextScale();
        GlStateManager.pushMatrix();           
        GlStateManager.translate(1.0F, ((this.getHeight() / 2.0F) - this.textHeight(textScale)) / 2.0F + 1.0F, 0.0F); 
        GlStateManager.scale(textScale, textScale, 0.0F); 
        this.mc.fontRenderer.drawString(this.getDisplayText(), 0, 0, textColor, false);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();           
        GlStateManager.translate(3.0F, this.getHeight() / 2.0F + ((this.getHeight() / 2.0F) - this.textHeight(textScale - 0.1F)) / 2.0F, 0.0F); 
        GlStateManager.scale(textScale - 0.1F, textScale - 0.1F, 0.0F); 
        this.mc.fontRenderer.drawString(this.senderName, 0, 0, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt(), false);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();           
        GlStateManager.translate(100.0F, ((this.getHeight() / 2.0F) - this.textHeight(textScale)) / 2.0F + 1.0F, 0.0F); 
        GlStateManager.scale(textScale, textScale, 0.0F); 
        this.mc.fontRenderer.drawString(this.giftTypeStr, 0, 0, textColor, false);
        GlStateManager.popMatrix();

        if (this.getWrapped().getType() == EnumGiftType.RETURNED) {
            GlStateManager.pushMatrix();           
            GlStateManager.translate(102.0F, this.getHeight() / 2.0F + ((this.getHeight() / 2.0F) - this.textHeight(textScale - 0.1F)) / 2.0F, 0.0F); 
            GlStateManager.scale(textScale - 0.1F, textScale - 0.1F, 0.0F); 
            this.mc.fontRenderer.drawString(this.returnedByStr, 0, 0, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt(), false);
            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();           
        GlStateManager.translate(180.0F, ((this.getHeight() / 2.0F) - this.textHeight(textScale)) / 2.0F + 1.0F, 0.0F); 
        GlStateManager.scale(textScale, textScale, 0.0F); 
        this.mc.fontRenderer.drawString(this.receiveDateStr, 0, 0, textColor, false);
        GlStateManager.popMatrix();

        if (!this.message.isEmpty()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);  

            this.mc.getTextureManager().bindTexture(OxygenGUITextures.NOTE_ICONS); 
            drawCustomSizedTexturedRect(this.getWidth() - 20, 2, noteIconU, 0, 6, 6, 18, 6); 
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (!this.message.isEmpty() && mouseX >= this.getX() + this.getWidth() - 20 && mouseY >= this.getY() + 2 && mouseX < this.getX() + this.getWidth() - 14 && mouseY < this.getY() + 8)
            this.drawMessage(mouseX, mouseY);
    }

    private void drawMessage(int mouseX, int mouseY) {
        int 
        lineHeight = 9,
        messageWidth = 128;
        GlStateManager.pushMatrix();           
        GlStateManager.translate(mouseX, mouseY - lineHeight * this.message.size(), 0.0F);            
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        //background
        drawRect(0, 0, 128, lineHeight * this.message.size(), this.tooltipBackgroundColor);

        //frame
        OxygenGUIUtils.drawRect(0.0D, 0.0D, 0.4D, (double) lineHeight * this.message.size(), this.tooltipFrameColor);
        OxygenGUIUtils.drawRect((double) messageWidth - 0.4D, 0.0D, (double) messageWidth, (double) lineHeight * this.message.size(), this.tooltipFrameColor);
        OxygenGUIUtils.drawRect(0.0D, 0.0D, (double) messageWidth, 0.4D, this.tooltipFrameColor);
        OxygenGUIUtils.drawRect(0.0D, (double) lineHeight * this.message.size() - 0.4D, (double) messageWidth, (double) lineHeight * this.message.size(), this.tooltipFrameColor);

        for (int i = 0; i < this.message.size(); i++) {
            GlStateManager.pushMatrix();           
            GlStateManager.translate(2.0F, i * lineHeight + (lineHeight - this.textHeight(this.getTooltipScaleFactor())) / 2 + 1.0F, 0.0F);            
            GlStateManager.scale(this.getTooltipScaleFactor(), this.getTooltipScaleFactor(), 0.0F);

            this.mc.fontRenderer.drawString(this.message.get(i), 0, 0, this.getEnabledTextColor(), false);

            GlStateManager.popMatrix();   
        }

        GlStateManager.popMatrix(); 
    }
}
