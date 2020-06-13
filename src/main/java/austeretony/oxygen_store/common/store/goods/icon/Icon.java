package austeretony.oxygen_store.common.store.goods.icon;

import java.io.BufferedOutputStream;
import java.io.IOException;

import austeretony.alternateui.screen.core.GUISimpleElement;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface Icon {

    EnumIconType getType();

    void write(BufferedOutputStream bos) throws IOException;

    void write(ByteBuf buffer);

    @SideOnly(Side.CLIENT)
    void draw(GUISimpleElement parent, int mouseX, int mouseY);

    @SideOnly(Side.CLIENT)
    void drawTooltip(GUISimpleElement parent, int mouseX, int mouseY);
}
