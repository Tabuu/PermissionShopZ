package nl.tabuu.permissionshopz.nodehandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.data.node.NodeType;
import nl.tabuu.permissionshopz.data.node.PermissionNode;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomHandler implements INodeHandler {

    private final String _command;

    public CustomHandler() {
        IConfiguration config = PermissionShopZ.getInstance().getConfiguration();
        _command = config.getString("CustomPermissionCommand");
    }

    @Override
    public boolean hasNode(Player player, Node node) {
        if(node instanceof PermissionNode) {
            PermissionNode permissionNode = (PermissionNode) node;
            return player.hasPermission(permissionNode.getPermission());
        } else return false;
    }

    @Override
    public boolean addNode(Player player, Node node) {
        if(node instanceof PermissionNode) {
            PermissionNode permissionNode = (PermissionNode) node;
            String formattedCommand = _command
                    .replace("{PLAYER}", player.getName())
                    .replace("{PERMISSION}", permissionNode.getPermission());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
            return true;
        }

        return false;
    }

    @Override
    public boolean isNodeTypeSupported(NodeType nodeType) {
        return NodeType.PERMISSION.equals(nodeType);
    }
}