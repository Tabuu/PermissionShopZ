package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public enum PermissionHandler {
    VAULT(VaultHandler.class, "Vault"),
    LUCK_PERMS(LuckPermsHandler.class, "Luck Perms"),
    GROUP_MANAGER(GroupManagerHandler.class, "Group Manager"),
    PERMISSIONS_EX(PermissionsExHandler.class, "PermissionsEx"),
    CUSTOM(CustomHandler.class, "Custom");

    private final String _name;
    private final Class<?> _class;
    private IPermissionHandler _handler;

    <T extends IPermissionHandler> PermissionHandler(Class<T> clazz, String name) {
        _class = clazz;
        _name = name;
    }

    public IPermissionHandler getHandler() {
        if(_handler == null) {
            try {
                _handler = (IPermissionHandler) _class.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException exception) {
                Bukkit.getPluginManager().disablePlugin(PermissionShopZ.getInstance());

                if(exception instanceof InvocationTargetException) {
                    InvocationTargetException targetException = (InvocationTargetException) exception;
                    Throwable throwable = targetException.getTargetException();

                    PermissionShopZ.getInstance().getLogger().severe(throwable.getMessage());
                }
                else exception.printStackTrace();
            }
        }

        return _handler;
    }

    public String getName() {
        return _name;
    }
}