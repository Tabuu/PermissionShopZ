package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.Tasks.BukkitPermsUpdateTask;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.entity.Player;

public class GroupManagerHandler implements IPermissionHandler {

    private GroupManager _groupManager;

    public GroupManagerHandler(GroupManager groupManager) {
        _groupManager = groupManager;
    }

    @Override
    public void addPermission(Player player, String permission) {
        OverloadedWorldHolder handler = _groupManager.getWorldsHolder().getWorldData(player);
        if (handler != null) {
            handler.getUser(player.getName()).addPermission(permission);
            BukkitPermsUpdateTask task = new BukkitPermsUpdateTask();
            task.run();
        } else
            PermissionShopZ.getInstance().getLogger().severe("Could not set permission of player " + player.getName());
    }
}
