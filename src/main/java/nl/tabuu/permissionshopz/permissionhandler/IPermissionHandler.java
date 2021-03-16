package nl.tabuu.permissionshopz.permissionhandler;

import org.bukkit.entity.Player;

public interface IPermissionHandler {

    default boolean hasNode(Player player, String node) {
        try{
            NodeType nodeType = NodeType.fromNode(node);
            if(!isNodeTypeSupported(nodeType)) return false;
            return hasNode(player, nodeType, node);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    boolean hasNode(Player player, NodeType nodeType, String node);

    default void addNode(Player player, String node) {
        NodeType nodeType = NodeType.fromNode(node);

        if(!isNodeTypeSupported(nodeType))
            throw new UnsupportedOperationException(String.format("Node type '%s' is not supported by this permission handler.", nodeType.toString()));

        addNode(player, nodeType, node);
    }

    void addNode(Player player, NodeType nodeType, String node);

    boolean isNodeTypeSupported(NodeType nodeType);
}