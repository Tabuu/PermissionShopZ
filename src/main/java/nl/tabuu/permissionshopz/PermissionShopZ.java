package nl.tabuu.permissionshopz;

import nl.tabuu.permissionshopz.bstats.Metrics;
import nl.tabuu.permissionshopz.command.PermissionShopCommand;
import nl.tabuu.permissionshopz.data.PerkManager;
import nl.tabuu.permissionshopz.permissionhandler.IPermissionHandler;
import nl.tabuu.permissionshopz.permissionhandler.PermissionHandler;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.file.JsonConfiguration;
import nl.tabuu.tabuucore.configuration.file.YamlConfiguration;
import nl.tabuu.tabuucore.plugin.TabuuCorePlugin;
import nl.tabuu.tabuucore.util.Dictionary;

import java.util.Objects;

public class PermissionShopZ extends TabuuCorePlugin {
    private static PermissionShopZ INSTANCE;

    private Dictionary _local;
    private PerkManager _manager;
    private IConfiguration _config, _data;
    private PermissionHandler _permissionHandler;

    @Override
    public void onEnable() {
        INSTANCE = this;

        _data = getConfigurationManager().addConfiguration("shop.json", JsonConfiguration.class);
        _config = getConfigurationManager().addConfiguration("config.yml", YamlConfiguration.class);
        _local = getConfigurationManager().addConfiguration("lang.yml", YamlConfiguration.class).getDictionary("");

        loadPerks();

        registerExecutors(new PermissionShopCommand());
        getPermissionHandler();

        Metrics metrics = new Metrics(this, 7110);
        Metrics.SimplePie handlerChart = new Metrics.SimplePie("permission_handler", _permissionHandler::getName);
        metrics.addCustomChart(handlerChart);

        getLogger().info("PermissionShopZ is now enabled.");
    }

    @Override
    public void onDisable() {
        savePerks();
        getLogger().info("PermissionShopZ is now disabled.");
    }

    public void reload() {
        savePerks();
        getConfigurationManager().reloadAll();
        loadPerks();
    }

    private void loadPerks() {
        _manager = _data.getSerializable("Shop", PerkManager.class, new PerkManager());
    }

    private void savePerks() {
        _data.set("Shop", _manager);
        _data.save();
    }

    public Dictionary getLocal() {
        return _local;
    }

    public IConfiguration getConfiguration() {
        return _config;
    }

    public PerkManager getPerkManager() {
        return _manager;
    }

    public IPermissionHandler getPermissionHandler() {
        if(_permissionHandler == null) {
            PermissionHandler handler = _config.get("PermissionManager", PermissionHandler::valueOf);
            Objects.requireNonNull(handler, "No permission handler specified");
            _permissionHandler = handler;
        }

        return _permissionHandler.getHandler();
    }

    public static PermissionShopZ getInstance() {
        return INSTANCE;
    }
}