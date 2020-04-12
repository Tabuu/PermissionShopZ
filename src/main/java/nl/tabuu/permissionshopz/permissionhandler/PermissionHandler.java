package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public enum PermissionHandler {
    VAULT(VaultHandler.class),
    LUCK_PERMS(LuckPermsHandler.class),
    GROUP_MANAGER(GroupManagerHandler.class),
    PERMISSIONS_EX(PermissionsExHandler.class),
    CUSTOM(CustomHandler.class);

    private Class<?> _class;
    private IPermissionHandler _handler;

    <T extends IPermissionHandler> PermissionHandler(Class<T> clazz) {
        _class = clazz;
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
}
