package nl.tabuu.permissionshop.permissionhandler;

import nl.tabuu.permissionshop.PermissionShop;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.Tasks.BukkitPermsUpdateTask;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.entity.Player;

public class PermissionHandler_GroupManager implements IPermissionHandler {

    GroupManager _groupManager;
    public PermissionHandler_GroupManager(GroupManager groupManager){
        _groupManager = groupManager;
    }

    @Override
    public void addPermission(Player player, String permission) {
        OverloadedWorldHolder handler = _groupManager.getWorldsHolder().getWorldData(player);
        if(handler != null){
            handler.getUser(player.getName()).addPermission(permission);
            BukkitPermsUpdateTask task = new BukkitPermsUpdateTask();
            task.run();
        }
        else PermissionShop.getPlugin().getLogger().severe("Could not set permission of player " + player.getName());
    }
}
