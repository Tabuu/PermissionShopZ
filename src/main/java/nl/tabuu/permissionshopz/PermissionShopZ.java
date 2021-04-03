package nl.tabuu.permissionshopz;

import nl.tabuu.permissionshopz.bstats.Metrics;
import nl.tabuu.permissionshopz.command.PermissionShopCommand;
import nl.tabuu.permissionshopz.dao.DAO;
import nl.tabuu.permissionshopz.dao.NodeDAO;
import nl.tabuu.permissionshopz.dao.PerkDAO;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.nodehandler.INodeHandler;
import nl.tabuu.permissionshopz.nodehandler.NodeHandler;
import nl.tabuu.permissionshopz.util.NumberFormat;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.file.JsonConfiguration;
import nl.tabuu.tabuucore.configuration.file.YamlConfiguration;
import nl.tabuu.tabuucore.plugin.TabuuCorePlugin;
import nl.tabuu.tabuucore.util.Dictionary;

import java.util.Objects;

public class PermissionShopZ extends TabuuCorePlugin {
    private static PermissionShopZ INSTANCE;

    private Dictionary _locale;
    private PerkDAO _perkDao;
    private NodeDAO _nodeDao;

    private IConfiguration _config;
    private NodeHandler _permissionHandler;

    @Override
    public void onEnable() {
        INSTANCE = this;

        getConfigurationManager().addConfiguration("perks.json", JsonConfiguration.class);
        getConfigurationManager().addConfiguration("nodes.json", JsonConfiguration.class);

        _config = getConfigurationManager().addConfiguration("config.yml", YamlConfiguration.class);
        _locale = getConfigurationManager().addConfiguration("lang.yml", YamlConfiguration.class).getDictionary("");

        _nodeDao = new NodeDAO();
        _nodeDao.readAll();

        _perkDao = new PerkDAO();
        _perkDao.readAll();

        NumberFormat.reloadSuffixMap();

        registerExecutors(new PermissionShopCommand());
        getPermissionHandler();

        Metrics metrics = new Metrics(this, 7110);
        Metrics.SimplePie handlerChart = new Metrics.SimplePie("permission_handler", _permissionHandler::getName);
        metrics.addCustomChart(handlerChart);

        getLogger().info("PermissionShopZ is now enabled.");
    }

    @Override
    public void onDisable() {
        _perkDao.writeAll();
        getLogger().info("PermissionShopZ is now disabled.");
    }

    public void reload() {
        _perkDao.writeAll();
        getConfigurationManager().reloadAll();
        _locale = getConfigurationManager().addConfiguration("lang.yml", YamlConfiguration.class).getDictionary("");
        _perkDao.readAll();

        NumberFormat.reloadSuffixMap();
    }

    public Dictionary getLocale() {
        return _locale;
    }

    public IConfiguration getConfiguration() {
        return _config;
    }

    public DAO<Integer, Perk> getPerkDao() {
        return _perkDao;
    }

    public NodeDAO getNodeDao() {
        return _nodeDao;
    }

    public INodeHandler getPermissionHandler() {
        if(_permissionHandler == null) {
            NodeHandler handler = _config.get("PermissionManager", NodeHandler::valueOf);
            Objects.requireNonNull(handler, "No permission handler specified");
            _permissionHandler = handler;
        }

        return _permissionHandler.getHandler();
    }

    public static PermissionShopZ getInstance() {
        return INSTANCE;
    }
}