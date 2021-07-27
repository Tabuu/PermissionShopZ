package nl.tabuu.permissionshopz.data.node;

import nl.tabuu.permissionshopz.PermissionShopZ;

public enum NodeType {
    PERMISSION,
    TEMPORARY_PERMISSION,
    GROUP,
    TRACK,
    UNKNOWN;

    @Override
    public String toString() {
        String locale = String.format("NODE_TYPE_%s", name().toUpperCase());
        return PermissionShopZ.getInstance().getLocale().translate(locale);
    }
}