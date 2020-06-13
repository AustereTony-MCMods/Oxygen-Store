package austeretony.oxygen_store.common.store;

public enum EnumWidgetSize {

    SMALL(84, 56),
    BIG(170, 56);

    private final int width, height;

    EnumWidgetSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
