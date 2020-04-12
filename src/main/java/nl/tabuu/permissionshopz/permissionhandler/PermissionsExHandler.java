package nl.tabuu.permissionshopz.permissionhandler;

import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExHandler implements IPermissionHandler{
    @Override
    public void addPermission(Player player, String permission) {
        PermissionUser target = PermissionsEx.getUser(player);
        target.addPermission(permission);
    }
}
