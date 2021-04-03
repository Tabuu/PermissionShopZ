package nl.tabuu.permissionshopz.nodehandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.node.GroupNode;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.data.node.NodeType;
import nl.tabuu.permissionshopz.data.node.PermissionNode;
import nl.tabuu.permissionshopz.nodehandler.exception.NodeHandlerNotFoundException;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.Tasks.BukkitPermsUpdateTask;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class GroupManagerHandler implements INodeHandler {

    private final GroupManager _groupManager;

    public GroupManagerHandler() {
        Plugin groupManagerPlugin = Bukkit.getPluginManager().getPlugin("GroupManager");
        if (!(groupManagerPlugin instanceof GroupManager))
            throw new NodeHandlerNotFoundException("Could not find GroupManager.");

        _groupManager = (GroupManager) Bukkit.getServer().getPluginManager().getPlugin("GroupManager");
    }

    @Override
    public boolean hasNode(Player player, Node node) {
        OverloadedWorldHolder handler = _groupManager.getWorldsHolder().getWorldData(player);
        if(Objects.isNull(handler)) {
            PermissionShopZ.getInstance().getLogger().severe("Could not get permissions of player " + player.getName());
            return false;
        }

        User user = handler.getUser(player.getName());
        NodeType nodeType = node.getType();

        if(Objects.isNull(user)) return false;

        switch (nodeType) {
            case GROUP:
                GroupNode groupNode = (GroupNode) node;
                String groupId = groupNode.getGroupId();
                if(!handler.groupExists(groupId)) return false;
                Group group = handler.getGroup(groupId);
                return user.containsSubGroup(group);

            case PERMISSION:
            case TEMPORARY_PERMISSION:
                PermissionNode permissionNode = (PermissionNode) node;
                return user.hasSamePermissionNode(permissionNode.getPermission());

            default:
                return false;
        }
    }

    @Override
    public boolean addNode(Player player, Node node) {
        OverloadedWorldHolder handler = _groupManager.getWorldsHolder().getWorldData(player);
        if(handler == null) {
            PermissionShopZ.getInstance().getLogger().severe("Could not set permission of player " + player.getName());
            return false;
        }

        NodeType nodeType = node.getType();
        User user = handler.getUser(player.getName());
        if(Objects.isNull(user)) return false;

        switch (nodeType) {
            case GROUP:
                GroupNode groupNode = (GroupNode) node;
                String groupId = groupNode.getGroupId();
                if(!handler.groupExists(groupId)) return false;
                Group group = handler.getGroup(groupId);
                user.addSubGroup(group);
                break;

            case PERMISSION:
                PermissionNode permissionNode = (PermissionNode) node;
                user.addPermission(permissionNode.getPermission());
                break;

            default:
                return false;
        }

        BukkitPermsUpdateTask task = new BukkitPermsUpdateTask();
        task.run();
        return true;
    }

    @Override
    public boolean isNodeTypeSupported(NodeType nodeType) {
        switch (nodeType) {
            case GROUP:
            case PERMISSION:
                return true;

            default:
                return false;
        }
    }
}