package austeretony.oxygen_store.common.store.goods.icon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import com.google.gson.JsonObject;

import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;

public enum EnumIconType {

    ITEM {

        @Override
        public Icon fromJson(JsonObject jsonObject) {
            return IconItem.fromJson(jsonObject);
        }

        @Override
        public Icon read(ByteBuf buffer) {
            return IconItem.read(buffer);
        }

        @Override
        public Icon read(BufferedInputStream bis) throws IOException {
            return IconItem.read(bis);
        }
    },
    TEXTURE {

        @Override
        public Icon fromJson(JsonObject jsonObject) {
            return IconTexture.fromJson(jsonObject);
        }

        @Override
        public Icon read(ByteBuf buffer) {
            return IconTexture.read(buffer);
        }

        @Override
        public Icon read(BufferedInputStream bis) throws IOException {
            return IconTexture.read(bis);
        }
    };

    public abstract Icon fromJson(JsonObject jsonObject);

    public abstract Icon read(ByteBuf buffer);

    public abstract Icon read(BufferedInputStream bis) throws IOException;

    public static Icon loadIcon(JsonObject jsonObject) {
        return EnumIconType.valueOf(jsonObject.get("type").getAsString()).fromJson(jsonObject);
    }

    public static void writeIcon(Icon icon, ByteBuf buffer) {
        buffer.writeByte(icon.getType().ordinal());
        icon.write(buffer);
    }

    public static Icon readIcon(ByteBuf buffer) {
        return EnumIconType.values()[buffer.readByte()].read(buffer);
    }

    public static void writeIcon(Icon icon, BufferedOutputStream bos) throws IOException {
        StreamUtils.write((byte) icon.getType().ordinal(), bos);
        icon.write(bos);
    }

    public static Icon readIcon(BufferedInputStream bis) throws IOException {
        return EnumIconType.values()[StreamUtils.readByte(bis)].read(bis);
    }
}
