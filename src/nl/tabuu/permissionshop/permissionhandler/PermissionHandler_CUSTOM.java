package nl.tabuu.permissionshop.permissionhandler;

import nl.tabuu.permissionshop.PermissionShop;
import nl.tabuu.tabuucore.configuration.LanguageConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionHandler_CUSTOM implements IPermissionHandler {

    LanguageConfig _languageConfig;

    public PermissionHandler_CUSTOM(){
        _languageConfig = PermissionShop.getConfigManager().getConfig("config").toLanguageConfig();
    }

    @Override
    public void addPermission(Player player, String permission) {
        String command = _languageConfig.parseText("CustomPermissionCommand", "{PLAYER}", player.getName(), "{PERMISSION}", permission);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
