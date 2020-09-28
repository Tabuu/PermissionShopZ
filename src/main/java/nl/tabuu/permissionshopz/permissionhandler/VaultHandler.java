package nl.tabuu.permissionshopz.permissionhandler;

import net.milkbowl.vault.permission.Permission;
import nl.tabuu.permissionshopz.permissionhandler.exception.PermissionHandlerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHandler implements IPermissionHandler{
    private Permission _permission;

    public VaultHandler() {
        RegisteredServiceProvider<Permission> provider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if(provider == null) throw new PermissionHandlerNotFoundException("Could not find Vault.");

        _permission = provider.getProvider();
    }

    @Override
    public void addPermissionNode(Player player, String permission) {
        _permission.playerAdd(null, player, permission);
    }

    @Override
    public boolean hasPermissionNode(Player player, String node) {
        return _permission.has(player, node);
    }
}