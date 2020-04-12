package nl.tabuu.permissionshopz;

import net.luckperms.api.LuckPerms;
import nl.tabuu.permissionshopz.bstats.Metrics;
import nl.tabuu.permissionshopz.command.PermissionShopCommand;
import nl.tabuu.permissionshopz.data.PerkManager;
import nl.tabuu.permissionshopz.permissionhandler.*;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.plugin.TabuuCorePlugin;
import nl.tabuu.tabuucore.util.Dictionary;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.*;

public class PermissionShopZ extends TabuuCorePlugin {
    private static PermissionShopZ INSTANCE;

    private Dictionary _local;
    private PerkManager _manager;
    private IConfiguration _config;
    private IPermissionHandler _permissionHandler;

    @Override
    public void onEnable() {
        INSTANCE = this;

        _config = getConfigurationManager().addConfiguration("config");
        _local = getConfigurationManager().addConfiguration("lang").getDictionary("");

        _manager = new PerkManager();
        load(new File(this.getDataFolder(), "shop.db"));

        setupPermissionHandler();

        new Metrics(this);
        getCommand("permissionshopz").setExecutor(new PermissionShopCommand());

        getLogger().info("PermissionShopZ is now enabled.");
    }

    private void setupPermissionHandler() {
        String managerName = _config.getString("PermissionManager");
        if(managerName == null) {
            getLogger().severe("Please specify a permission manager in the config.yml!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        managerName = managerName.toUpperCase();
        PluginManager pluginManager = getServer().getPluginManager();
        String errorMessage = null;

        switch (managerName) {
            case "GROUP_MANAGER":
                Plugin groupManagerPlugin = pluginManager.getPlugin("GroupManager");
                if(groupManagerPlugin instanceof GroupManager) {
                    GroupManager groupManager = (GroupManager) getServer().getPluginManager().getPlugin("GroupManager");
                    _permissionHandler = new GroupManagerHandler(groupManager);
                }
                else errorMessage = "GroupManager was not found! Please edit the config.yml";
                break;

            case "PERMISSIONS_EX":
                Plugin pexPlugin = getServer().getPluginManager().getPlugin("PermissionsEx");
                if (pexPlugin instanceof PermissionsEx) _permissionHandler = new PEXHandler();
                else errorMessage = "PermissionsEx was not found! Please edit the config.yml";
                break;

            case "LUCK_PERMS":
                RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
                if (provider != null) _permissionHandler = new LuckPermsHandler();
                else errorMessage = "LuckPerms provider was not found! Please edit the config.yml";
                break;

            case "CUSTOM":
                _permissionHandler = new CustomHandler();
                break;

            default:
                errorMessage = "No valid permission manager found! Please edit the config.yml";
                break;
        }

        if(_permissionHandler == null) {
            getLogger().severe(errorMessage);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        save(new File(getDataFolder(), "shop.db"));

        getLogger().info("PermissionShopZ is now disabled.");
    }

    public void save(File file) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            objectOutputStream.writeObject(_manager);
        } catch (IOException exception) {
            exception.printStackTrace();
            getLogger().severe("Could not save data!");
        }
    }

    private void load(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            _manager = (PerkManager) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            getLogger().warning("No data found!");
        }
    }

    public void reload() {
        File file = new File(getDataFolder(), "shop.db");
        save(file);
        load(file);

        getConfigurationManager().reloadAll();
    }

    public Dictionary getLocal() {
        return _local;
    }

    public IPermissionHandler getPermissionHandler() {
        return _permissionHandler;
    }

    public PerkManager getPerkManager() {
        return _manager;
    }

    public static PermissionShopZ getInstance() {
        return INSTANCE;
    }
}
