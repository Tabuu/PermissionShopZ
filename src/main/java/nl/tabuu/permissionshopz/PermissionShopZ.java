package nl.tabuu.permissionshopz;

import nl.tabuu.permissionshopz.bstats.Metrics;
import nl.tabuu.permissionshopz.command.PermissionShopCommand;
import nl.tabuu.permissionshopz.data.PerkManager;
import nl.tabuu.permissionshopz.permissionhandler.IPermissionHandler;
import nl.tabuu.permissionshopz.permissionhandler.PermissionHandler;
import nl.tabuu.permissionshopz.permissionhandler.exception.PermissionHandlerNotFoundException;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.plugin.TabuuCorePlugin;
import nl.tabuu.tabuucore.util.Dictionary;

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
        PermissionHandler handler = _config.getEnum(PermissionHandler.class, "PermissionManager");
        if(handler == null)
            throw new PermissionHandlerNotFoundException("No permission handler specified");

        _permissionHandler = handler.getHandler();
    }

    @Override
    public void onDisable() {
        save(new File(getDataFolder(), "shop.db"));

        getLogger().info("PermissionShopZ is now disabled.");
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

    private void save(File file) {
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
}
