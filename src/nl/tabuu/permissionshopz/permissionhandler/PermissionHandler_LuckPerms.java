package nl.tabuu.permissionshopz.permissionhandler;

import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionHandler_LuckPerms implements IPermissionHandler {

    private LuckPermsApi _luckPermsApi;

    public PermissionHandler_LuckPerms(){
        RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
        _luckPermsApi = provider.getProvider();
    }

    @Override
    public void addPermission(Player player, String permission) {
        Node node = _luckPermsApi.getNodeFactory().newBuilder(permission).build();
        User user = _luckPermsApi.getUser(player.getUniqueId());

        user.setPermission(node);
        user.getCachedData().reloadPermissions();

        _luckPermsApi.getUserManager().saveUser(user);
    }
}
