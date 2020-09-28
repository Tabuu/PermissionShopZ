package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.tabuucore.serialization.string.Serializer;
import org.bukkit.entity.Player;

public interface IPermissionHandler {

    default boolean hasPermission(Player player, String permissions) {
        String node = getNode(permissions);
        return hasPermissionNode(player, node);
    }

    default boolean hasPermissionNode(Player player, String node) {
        return player.hasPermission(node);
    }

    default void addPermission(Player player, String permission) {
        String node = getNode(permission);
        Long duration = getDuration(permission);

        if (duration == null) addPermissionNode(player, node);
        else addTimedPermissionNode(player, node, duration);
    }

    void addPermissionNode(Player player, String permission);

    default void addTimedPermissionNode(Player player, String permission, long lifeTime) {
        throw new UnsupportedOperationException("The current permission handler does not support timed permissions");
    }

    default boolean isTimedPermission(String permission) {
        return permission.contains(":");
    }

    default String getNode(String permission) {
        return permission.split(":")[0];
    }

    default Long getDuration(String permission) {
        String[] args = permission.split(":");

        if(args.length < 2) return null;
        return Serializer.TIME.deserialize(args[1]);
    }

    default boolean isTimedPermissionSupported() {
        return false;
    }

}