package nl.tabuu.permissionshopz.permissionhandler;

import org.bukkit.entity.Player;

public interface IPermissionHandler {
    void addPermission(Player player, String permission);
}