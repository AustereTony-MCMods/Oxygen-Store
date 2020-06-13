package austeretony.oxygen_store.common.store.goods.icon;

public abstract class AbstractIcon implements Icon {

    protected final int x, y;

    public AbstractIcon(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
