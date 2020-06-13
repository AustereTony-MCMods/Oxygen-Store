package austeretony.oxygen_store.common.main;

import austeretony.oxygen_core.client.api.ClientReference;

public enum EnumStoreStatusMessage {

    PURCHASE_SUCCESSFUL("purchaseSuccessful"),
    OPERATION_FAILED("operationFailed"),

    GIFT_ACCEPTED_SUCCESSFULY("giftAcceptedSuccessfuly"),
    GIFT_RETURNED_SUCCESSFULY("giftReturnedSuccessfuly"),
    GIFT_SENT_SUCCESSFULY("giftSentSuccessfuly"),

    OFFERS_RELOADED("offersReloaded");

    private final String status;

    EnumStoreStatusMessage(String status) {
        this.status = "oxygen_store.status.message." + status;
    }

    public String localizedName() {
        return ClientReference.localize(this.status);
    }
}
