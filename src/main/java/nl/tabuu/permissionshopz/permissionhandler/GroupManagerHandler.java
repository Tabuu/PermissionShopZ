package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.permissionhandler.exception.PermissionHandlerNotFoundException;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.Tasks.BukkitPermsUpdateTask;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class GroupManagerHandler implements IPermissionHandler {

    private final GroupManager _groupManager;

    public GroupManagerHandler() {
        Plugin groupManagerPlugin = Bukkit.getPluginManager().getPlugin("GroupManager");
        if (!(groupManagerPlugin instanceof GroupManager))
            throw new PermissionHandlerNotFoundException("Could not find GroupManager.");

        _groupManager = (GroupManager) Bukkit.getServer().getPluginManager().getPlugin("GroupManager");
    }

    @Override
    public boolean hasNode(Player player, NodeType nodeType, String node) {
        OverloadedWorldHolder handler = _groupManager.getWorldsHolder().getWorldData(player);
        if(Objects.isNull(handler)) {
            PermissionShopZ.getInstance().getLogger().severe("Could not get permissions of player " + player.getName());
            return false;
        }

        String value = nodeType.getValue(node);
        User user = handler.getUser(player.getName());

        if(Objects.isNull(value) || Objects.isNull(user)) return false;

        switch (nodeType) {
            case GROUP:
                if(!handler.groupExists(value)) return false;
                Group group = handler.getGroup(value);
                return user.containsSubGroup(group);

            case PERMISSION:
            case TEMPORARY_PERMISSION:
                return user.hasSamePermissionNode(value);

            default:
                return false;
        }
    }

    @Override
    public void addNode(Player player, NodeType nodeType, String node) {
        OverloadedWorldHolder handler = _groupManager.getWorldsHolder().getWorldData(player);
        if(handler == null) {
            PermissionShopZ.getInstance().getLogger().severe("Could not set permission of player " + player.getName());
            return;
        }

        String value = nodeType.getValue(node);
        User user = handler.getUser(player.getName());
        if(Objects.isNull(value) || Objects.isNull(user)) return;

        switch (nodeType) {
            case GROUP:
                if(!handler.groupExists(value)) return;
                Group group = handler.getGroup(value);
                user.addSubGroup(group);
                break;

            case PERMISSION:
            case TEMPORARY_PERMISSION:
                user.addPermission(value);
                break;

            default:
                return;
        }

        BukkitPermsUpdateTask task = new BukkitPermsUpdateTask();
        task.run();
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