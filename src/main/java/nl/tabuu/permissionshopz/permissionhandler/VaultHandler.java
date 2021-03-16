package nl.tabuu.permissionshopz.permissionhandler;

import net.milkbowl.vault.permission.Permission;
import nl.tabuu.permissionshopz.permissionhandler.exception.PermissionHandlerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Objects;

public class VaultHandler implements IPermissionHandler{
    private final Permission _permission;

    public VaultHandler() {
        RegisteredServiceProvider<Permission> provider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if(provider == null) throw new PermissionHandlerNotFoundException("Could not find Vault.");

        _permission = provider.getProvider();
    }

    @Override
    public boolean hasNode(Player player, NodeType nodeType, String node) {
        String value = nodeType.getValue(node);

        if(Objects.isNull(value)) return false;

        switch (nodeType) {
            case PERMISSION:
            case TEMPORARY_PERMISSION:
                return _permission.has(player, value);

            case GROUP:
                return _permission.playerInGroup(null, player, value);
        }

        return false;
    }

    @Override
    public void addNode(Player player, NodeType nodeType, String node) {
        String value = nodeType.getValue(node);

        if(Objects.isNull(value)) return;

        switch (nodeType) {
            case PERMISSION:
            case TEMPORARY_PERMISSION:
                _permission.playerAdd(null, player, value);

            case GROUP:
                _permission.playerAddGroup(null, player, value);
        }
    }

    @Override
    public boolean isNodeTypeSupported(NodeType nodeType) {
        switch (nodeType) {
            case PERMISSION:
                return true;

            case GROUP:
                return _permission.hasGroupSupport();

            default:
                return false;
        }
    }
}