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
import org.bukkit.plugin.RegisteredServiceProvider;

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

        this.getCommand("permissionshopz").setExecutor(new PermissionShopCommand());
        new Metrics(this);
        this.getLogger().info("PermissionShopZ is now enabled.");
    }

    private void setupPermissionHandler() {
        switch (_config.getString("PermissionManager").toUpperCase()) {
            case "GROUP_MANAGER":
                try {
                    GroupManager groupManager = (GroupManager) this.getServer().getPluginManager().getPlugin("GroupManager");
                    _permissionHandler = new PermissionHandler_GroupManager(groupManager);
                } catch (Exception e) {
                    this.getLogger().severe("GroupManager was not found! Please edit the config.yml");
                    this.getServer().getPluginManager().disablePlugin(this);
                }
                break;

            case "PERMISSIONS_EX":
                if (this.getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
                    _permissionHandler = new PermissionHandler_PEX();
                } else {
                    this.getLogger().severe("PermissionsEx was not found! Please edit the config.yml");
                    this.getServer().getPluginManager().disablePlugin(this);
                }
                break;

            case "LUCK_PERMS":
                RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
                if (provider != null) {
                    _permissionHandler = new PermissionHandler_LuckPerms();
                } else {
                    this.getLogger().severe("LuckPerms provider was not found! Please edit the config.yml");
                    this.getServer().getPluginManager().disablePlugin(this);
                }
                break;

            case "CUSTOM":
                _permissionHandler = new PermissionHandler_CUSTOM();
                break;

            default:
                this.getLogger().severe("No valid permission manager found! Please edit the config.yml");
                this.getServer().getPluginManager().disablePlugin(this);
                break;
        }
    }

    @Override
    public void onDisable() {
        save(new File(this.getDataFolder(), "shop.db"));
        this.getLogger().info("PermissionShopZ is now disabled.");
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

        this.getConfigurationManager().reloadAll();
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
