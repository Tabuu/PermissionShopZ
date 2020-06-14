package nl.tabuu.permissionshopz.permissionhandler;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import nl.tabuu.permissionshopz.permissionhandler.exception.PermissionHandlerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsHandler implements IPermissionHandler {

    private LuckPerms _luckPerms;

    public LuckPermsHandler(){
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) throw new PermissionHandlerNotFoundException("Could not find LuckPerms.");
        else _luckPerms = provider.getProvider();
    }

    @Override
    public void addPermission(Player player, String permission) {
        PermissionNode node = PermissionNode.builder(permission).build();
        User user = _luckPerms.getUserManager().getUser(player.getUniqueId());
        if(user == null) return;
        
        user.data().add(node);
        _luckPerms.getUserManager().saveUser(user);
    }
}
