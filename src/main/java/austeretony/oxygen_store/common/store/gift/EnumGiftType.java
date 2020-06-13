package austeretony.oxygen_store.common.store.gift;

import austeretony.oxygen_core.client.api.ClientReference;

public enum EnumGiftType {

    PENDING("pending"),
    RETURNED("returned");

    private final String name;

    EnumGiftType(String name) {
        this.name = name;
    }

    public String localized() {
        return ClientReference.localize("oxygen_store.gift.type." + this.name);
    }
}
