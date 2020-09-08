package nl.tabuu.permissionshopz.permissionhandler;

import org.bukkit.entity.Player;

public interface IPermissionHandler {

    void addPermission(Player player, String permission);

    default void addTimedPermission(Player player, String permission, long lifeTime) {
        throw new UnsupportedOperationException("The current permission handler does not support timed permissions");
    }

    default boolean isTimedPermissionSupported() {
        return false;
    }

}