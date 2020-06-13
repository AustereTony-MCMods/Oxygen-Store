package austeretony.oxygen_store.common.store.goods.icon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.alternateui.util.UIUtils;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.OxygenGUIUtils;
import austeretony.oxygen_core.client.util.ClientUtils;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.FilesUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_store.client.gui.store.offers.StoreOfferWidget;
import austeretony.oxygen_store.common.store.StoreOffer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IconTexture extends AbstractIcon {

    private final byte[] textureRaw;

    private final String tooltip;

    //client only
    private ResourceLocation texture;

    private boolean hovered;

    private int linesAmount, tooltipWidth;

    private String[] tooltipLines;

    private IconTexture(int x, int y, String tooltip, byte[] textureRaw) {
        super(x, y);
        this.tooltip = tooltip;
        this.textureRaw = textureRaw;
    }

    public static AbstractIcon fromJson(JsonObject object) {
        String iconName = object.get("icon").getAsString();
        byte[] textureRaw = FilesUtils.loadImageBytes(
                CommonReference.getGameFolder() + "/config/oxygen/data/server/store/offers/textures/" + iconName, 
                null);
        return new IconTexture(
                object.get("x").getAsInt(),
                object.get("y").getAsInt(),
                object.get("tooltip").getAsString(),
                textureRaw);
    }

    @Override
    public EnumIconType getType() {
        return EnumIconType.TEXTURE;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.textureRaw.length, bos);
        StreamUtils.write(this.textureRaw, bos);

        StreamUtils.write((short) this.x, bos);
        StreamUtils.write((short) this.y, bos);
        StreamUtils.write(this.tooltip, bos);
    }

    public static AbstractIcon read(BufferedInputStream bis) throws IOException {
        byte[] textureRaw = new byte[StreamUtils.readInt(bis)];
        StreamUtils.readBytes(textureRaw, bis);
        return new IconTexture(
                StreamUtils.readShort(bis), 
                StreamUtils.readShort(bis), 
                StreamUtils.readString(bis),
                textureRaw);
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(this.textureRaw.length);
        buffer.writeBytes(this.textureRaw);

        buffer.writeShort(this.x);
        buffer.writeShort(this.y);
        ByteBufUtils.writeString(this.tooltip, buffer);
    }

    public static AbstractIcon read(ByteBuf buffer) {
        byte[] textureRaw = new byte[buffer.readInt()];
        buffer.readBytes(textureRaw);
        return new IconTexture(
                buffer.readShort(), 
                buffer.readShort(), 
                ByteBufUtils.readString(buffer),
                textureRaw);
    }

    @Nonnull
    public ResourceLocation getTexture() {
        if (this.texture == null)
            this.texture = ClientUtils.getTexturePathFromBytes(this.textureRaw);
        return this.texture;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GUISimpleElement parent, int mouseX, int mouseY) {
        Minecraft mc = ClientReference.getMinecraft();

        GlStateManager.pushMatrix();           
        GlStateManager.translate(this.x, this.y, 0.0F);           
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableBlend(); 
        mc.getTextureManager().bindTexture(this.getTexture());
        GUIAdvancedElement.drawCustomSizedTexturedRect(0, 0, 0, 0, 16, 16, 16, 16);                 
        GlStateManager.disableBlend();  

        this.hovered = false;
        if (mouseX >= parent.getX() + this.x && mouseY >= parent.getY() + this.y && mouseX < parent.getX() + this.x + 18 && mouseY < parent.getY() + this.y + 18) {
            GUISimpleElement.drawRect(0, 0, 16, 16, 0x45ffffff);//hovering     
            this.hovered = true;
        }

        GlStateManager.popMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawTooltip(GUISimpleElement parent, int mouseX, int mouseY) {
        if (this.hovered) {
            Minecraft mc = ClientReference.getMinecraft();
            float scale = EnumBaseGUISetting.TEXT_TOOLTIP_SCALE.get().asFloat();
            int height = 9;

            this.tooltipLines = this.tooltip.split("\n");
            int i, tempWidth, roleId;
            String line;
            for (i = 0; i < this.tooltipLines.length; i++) {
                line = this.tooltipLines[i];
                this.linesAmount = i + 1;
                tempWidth = (int) (UIUtils.getTextWidth(line, scale) + 4);
                if (this.tooltipWidth < tempWidth)
                    this.tooltipWidth = tempWidth;
            }

            GlStateManager.pushMatrix();           
            GlStateManager.translate(mouseX, mouseY - height * this.linesAmount, 0.0F);            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            //background
            GUISimpleElement.drawRect(0, 0, this.tooltipWidth, height * this.linesAmount, EnumBaseGUISetting.BACKGROUND_BASE_COLOR.get().asInt());

            //frame
            OxygenGUIUtils.drawRect(0.0D, 0.0D, 0.4D, (double) height * this.linesAmount, EnumBaseGUISetting.BACKGROUND_ADDITIONAL_COLOR.get().asInt());
            OxygenGUIUtils.drawRect((double) this.tooltipWidth - 0.4D, 0.0D, (double) this.tooltipWidth, (double) height * this.linesAmount, EnumBaseGUISetting.BACKGROUND_ADDITIONAL_COLOR.get().asInt());
            OxygenGUIUtils.drawRect(0.0D, 0.0D, (double) this.tooltipWidth, 0.4D, EnumBaseGUISetting.BACKGROUND_ADDITIONAL_COLOR.get().asInt());
            OxygenGUIUtils.drawRect(0.0D, (double) height * this.linesAmount - 0.4D, (double) this.tooltipWidth, (double) height * this.linesAmount, EnumBaseGUISetting.BACKGROUND_ADDITIONAL_COLOR.get().asInt());

            for (i = 0; i < this.linesAmount; i++) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(2.0F, i * height + (height - UIUtils.getTextHeight(scale)) / 2.0F + 1.0F, 0.0F);            
                GlStateManager.scale(scale, scale, 0.0F);

                mc.fontRenderer.drawString(this.tooltipLines[i], 0, 0, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt(), false);

                GlStateManager.popMatrix();   
            }

            GlStateManager.popMatrix(); 
        }
    }
}
