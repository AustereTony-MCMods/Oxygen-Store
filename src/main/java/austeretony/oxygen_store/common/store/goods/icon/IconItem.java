package austeretony.oxygen_store.common.store.goods.icon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import com.google.gson.JsonObject;

import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.alternateui.util.UIUtils;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IconItem extends AbstractIcon {

    private final ItemStackWrapper stackWrapper;

    private final int amount;

    //client
    private boolean hovered;

    private IconItem(int x, int y, ItemStackWrapper stackWrapper, int amount) {
        super(x, y);
        this.stackWrapper = stackWrapper;
        this.amount = amount;
    }

    public static AbstractIcon fromJson(JsonObject object) {
        return new IconItem(
                object.get("x").getAsInt(),
                object.get("y").getAsInt(),
                ItemStackWrapper.fromJson(object.getAsJsonObject("itemstack")),
                object.get("amount").getAsInt());
    }

    @Override
    public EnumIconType getType() {
        return EnumIconType.ITEM;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.x, bos);
        StreamUtils.write((short) this.y, bos);
        this.stackWrapper.write(bos);
        StreamUtils.write((short) this.amount, bos);
    }

    public static AbstractIcon read(BufferedInputStream bis) throws IOException {
        return new IconItem(
                StreamUtils.readShort(bis), 
                StreamUtils.readShort(bis), 
                ItemStackWrapper.read(bis), 
                StreamUtils.readShort(bis));
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeShort(this.x);
        buffer.writeShort(this.y);
        this.stackWrapper.write(buffer);
        buffer.writeShort(this.amount);
    }

    public static AbstractIcon read(ByteBuf buffer) {
        return new IconItem(
                buffer.readShort(), 
                buffer.readShort(), 
                ItemStackWrapper.read(buffer),
                buffer.readShort());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GUISimpleElement parent, int mouseX, int mouseY) {
        Minecraft mc = ClientReference.getMinecraft();

        GlStateManager.pushMatrix();           
        GlStateManager.translate(this.x, this.y, 0.0F);           
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GUISimpleElement.drawRect(0, 0, 16, 16, EnumBaseGUISetting.BACKGROUND_BASE_COLOR.get().asInt());//background

        RenderHelper.enableGUIStandardItemLighting();            
        GlStateManager.enableDepth();

        mc.getRenderItem().renderItemAndEffectIntoGUI(this.stackWrapper.getCachedItemStack(), 0, 0);  

        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();

        if (this.amount > 1) {
            float scale = EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F;
            String amountStr = String.format("x%s", this.amount);

            GlStateManager.pushMatrix();           
            GlStateManager.translate(16.0F - UIUtils.getTextWidth(amountStr, scale), 12.0F, 0.0F);            
            GlStateManager.scale(scale, scale, 0.0F);   
            mc.fontRenderer.drawString(amountStr, 0, 0, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt(), true);           
            GlStateManager.popMatrix();      
        }

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
        if (this.hovered)
            parent.getScreen().drawToolTip(this.stackWrapper.getCachedItemStack(), mouseX, mouseY);
    }
}
