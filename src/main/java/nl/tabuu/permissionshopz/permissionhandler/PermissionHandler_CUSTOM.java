package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionHandler_CUSTOM implements IPermissionHandler {

    private String _command;

    public PermissionHandler_CUSTOM(){
        _command = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("config").getString("CustomPermissionCommand");
    }

    @Override
    public void addPermission(Player player, String permission) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), _command.replace("{PLAYER}", player.getName()).replace("{PERMISSION}", permission));
    }
}
