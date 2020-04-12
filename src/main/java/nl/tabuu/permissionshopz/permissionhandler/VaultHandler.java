package nl.tabuu.permissionshopz.permissionhandler;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

public class VaultHandler implements IPermissionHandler{
    private Permission _permission;

    public VaultHandler(Permission permission) {
        _permission = permission;
    }

    @Override
    public void addPermission(Player player, String permission) {
        _permission.playerAdd(player, permission);
    }
}
