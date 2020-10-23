package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.permissionhandler.exception.PermissionHandlerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExHandler implements IPermissionHandler {

    public PermissionsExHandler() {
        Plugin pexPlugin = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
        if (!(pexPlugin instanceof PermissionsEx))
            throw new PermissionHandlerNotFoundException("Could not find PermissionsEx.");
    }

    @Override
    public void addPermissionNode(Player player, String permission) {
        PermissionUser target = PermissionsEx.getUser(player);
        target.addPermission(permission);
    }

    @Override
    public void addTimedPermissionNode(Player player, String permission, long lifeTime) {
        int timeInSeconds = (int) (lifeTime / 1000L);
        PermissionUser target = PermissionsEx.getUser(player);
        target.addTimedPermission(permission, null, timeInSeconds);
    }

    @Override
    public boolean hasPermissionNode(Player player, String node) {
        return PermissionsEx.getUser(player).has(node);
    }

    @Override
    public boolean isTimedPermissionSupported() {
        return true;
    }
}