package nl.tabuu.permissionshopz.nodehandler;

import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.data.node.NodeType;
import org.bukkit.entity.Player;

public interface INodeHandler {
    boolean hasNode(Player player, Node node);

    boolean addNode(Player player, Node node);

    boolean isNodeTypeSupported(NodeType nodeType);
}