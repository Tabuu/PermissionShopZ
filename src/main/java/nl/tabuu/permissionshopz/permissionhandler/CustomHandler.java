package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomHandler implements IPermissionHandler {

    private final String _command;

    public CustomHandler() {
        IConfiguration config = PermissionShopZ.getInstance().getConfiguration();
        _command = config.getString("CustomPermissionCommand");
    }

    @Override
    public boolean hasNode(Player player, NodeType nodeType, String node) {
        String value = nodeType.getValue(node);

        if (NodeType.PERMISSION.equals(nodeType))
            return player.hasPermission(value);

        return false;
    }

    @Override
    public void addNode(Player player, NodeType nodeType, String node) {
        String value = nodeType.getValue(node);

        if (NodeType.PERMISSION.equals(nodeType)) {
            String formattedCommand = _command
                    .replace("{PLAYER}", player.getName())
                    .replace("{PERMISSION}", value);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
        }
    }

    @Override
    public boolean isNodeTypeSupported(NodeType nodeType) {
        return NodeType.PERMISSION.equals(nodeType);
    }
}