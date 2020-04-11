package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomHandler implements IPermissionHandler {

    private String _command;

    public CustomHandler() {
        IConfiguration config = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("config");
        _command = config.getString("CustomPermissionCommand");
    }

    @Override
    public void addPermission(Player player, String permission) {
        String formattedCommand = _command
                .replace("{PLAYER}", player.getName())
                .replace("{PERMISSION}", permission);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
    }
}
