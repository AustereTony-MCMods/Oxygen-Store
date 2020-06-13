package austeretony.oxygen_store.client;

import austeretony.oxygen_core.common.chat.ChatMessagesHandler;
import austeretony.oxygen_store.common.main.EnumStoreStatusMessage;
import austeretony.oxygen_store.common.main.StoreMain;

public class StoreStatusMessagesHandler implements ChatMessagesHandler {

    @Override
    public int getModIndex() {
        return StoreMain.STORE_MOD_INDEX;
    }

    @Override
    public String getMessage(int messageIndex) {
        return EnumStoreStatusMessage.values()[messageIndex].localizedName();
    }
}
