package nl.tabuu.permissionshopz.nodehandler;

import net.milkbowl.vault.permission.Permission;
import nl.tabuu.permissionshopz.data.node.GroupNode;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.data.node.NodeType;
import nl.tabuu.permissionshopz.data.node.PermissionNode;
import nl.tabuu.permissionshopz.nodehandler.exception.NodeHandlerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Objects;

public class VaultHandler implements INodeHandler {
    private final Permission _permission;

    public VaultHandler() {
        RegisteredServiceProvider<Permission> provider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if(provider == null) throw new NodeHandlerNotFoundException("Could not find Vault.");

        _permission = provider.getProvider();
    }

    @Override
    public boolean hasNode(Player player, Node node) {
        switch (node.getType()) {
            case PERMISSION:
            case TEMPORARY_PERMISSION:
                PermissionNode permNode = (PermissionNode) node;
                return _permission.has(player, permNode.getPermission());

            case GROUP:
                GroupNode groupNode = (GroupNode) node;
                return _permission.playerInGroup(null, player, groupNode.getGroupId());
        }

        return false;
    }

    @Override
    public boolean addNode(Player player, Node node) {
        switch (node.getType()) {
            case PERMISSION:
            case TEMPORARY_PERMISSION:
                PermissionNode permNode = (PermissionNode) node;
                _permission.playerAdd(null, player, permNode.getPermission());
                return true;

            case GROUP:
                GroupNode groupNode = (GroupNode) node;
                _permission.playerAddGroup(null, player, groupNode.getGroupId());
                return true;

            default:
                return false;
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