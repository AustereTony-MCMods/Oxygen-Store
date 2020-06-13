package austeretony.oxygen_store.common.main;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.privilege.PrivilegeRegistry;

public enum EnumStorePrivilege {

    STORE_ACCESS("store:storeAccess", 1700, EnumValueType.BOOLEAN);

    private final String name;

    private final int id;

    private final EnumValueType type;

    EnumStorePrivilege(String name, int id, EnumValueType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public int id() {
        return this.id;
    }

    public static void register() {
        for (EnumStorePrivilege privilege : values())
            PrivilegeRegistry.registerPrivilege(privilege.name, privilege.id, privilege.type);
    }
}
