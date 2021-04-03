package nl.tabuu.permissionshopz.nodehandler;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.node.NodeType;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public enum NodeHandler {
    /**
     * Supports {@link NodeType}s: {@link NodeType#PERMISSION}, and {@link NodeType#GROUP}
     */
    VAULT(VaultHandler.class, "Vault"),

    /**
     * Supports {@link NodeType}s: {@link NodeType#PERMISSION}, {@link NodeType#GROUP}, {@link NodeType#TEMPORARY_PERMISSION}, and {@link NodeType#TRACK}
     */
    LUCK_PERMS(LuckPermsHandler.class, "Luck Perms"),

    /**
     * Supports {@link NodeType}s: {@link NodeType#PERMISSION}, and {@link NodeType#GROUP}
     */
    GROUP_MANAGER(GroupManagerHandler.class, "Group Manager"),

    /**
     * Supports {@link NodeType}s: {@link NodeType#PERMISSION}, {@link NodeType#GROUP}, {@link NodeType#TEMPORARY_PERMISSION}, and {@link NodeType#TRACK}
     */
    PERMISSIONS_EX(PermissionsExHandler.class, "PermissionsEx"),

    /**
     * Supports {@link NodeType}s: {@link NodeType#PERMISSION}
     */
    CUSTOM(CustomHandler.class, "Custom");

    private final String _name;
    private final Class<?> _class;
    private INodeHandler _handler;

    <T extends INodeHandler> NodeHandler(Class<T> clazz, String name) {
        _class = clazz;
        _name = name;
    }

    public INodeHandler getHandler() {
        if(_handler == null) {
            try {
                _handler = (INodeHandler) _class.getDeclaredConstructor().newInstance();
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