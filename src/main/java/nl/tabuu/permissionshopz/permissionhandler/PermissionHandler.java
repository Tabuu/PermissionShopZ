package nl.tabuu.permissionshopz.permissionhandler;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;
import nl.tabuu.permissionshopz.exception.PermissionHandlerNotFoundException;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public enum PermissionHandler {
    VAULT {
        @Override
        protected IPermissionHandler loadHandler() {
            RegisteredServiceProvider<Permission> provider = Bukkit.getServicesManager().getRegistration(Permission.class);
            if(provider != null) {
                Permission permission = provider.getProvider();
                return new VaultHandler(permission);
            }
            throw new PermissionHandlerNotFoundException("Could not find Vault.");
        }
    },

    LUCK_PERMS {
        @Override
        protected IPermissionHandler loadHandler() {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) return new LuckPermsHandler();
            else throw new PermissionHandlerNotFoundException("Could not find LuckPerms.");
        }
    },

    GROUP_MANAGER {
        @Override
        protected IPermissionHandler loadHandler() {
            Plugin groupManagerPlugin = Bukkit.getPluginManager().getPlugin("GroupManager");
            if(groupManagerPlugin instanceof GroupManager) {
                GroupManager groupManager = (GroupManager) Bukkit.getServer().getPluginManager().getPlugin("GroupManager");
                return new GroupManagerHandler(groupManager);
            }
            else throw new PermissionHandlerNotFoundException("Could not find GroupManager.");
        }
    },

    PERMISSIONS_EX {
        @Override
        protected IPermissionHandler loadHandler() {
            Plugin pexPlugin = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
            if (pexPlugin instanceof PermissionsEx) return new PermissionsExHandler();
            else throw new PermissionHandlerNotFoundException("Could not find PermissionsEx.");
        }
    },

    CUSTOM {
        @Override
        protected IPermissionHandler loadHandler() {
            return new CustomHandler();
        }
    };

    private IPermissionHandler _handler;

    public IPermissionHandler getHandler() {
        if(_handler == null)
            _handler = loadHandler();
        return _handler;
    }

    protected abstract IPermissionHandler loadHandler();
}
