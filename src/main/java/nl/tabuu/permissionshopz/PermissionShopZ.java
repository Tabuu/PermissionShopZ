package nl.tabuu.permissionshopz;

import nl.tabuu.permissionshopz.bstats.Metrics;
import nl.tabuu.permissionshopz.command.PermissionShopCommand;
import nl.tabuu.permissionshopz.dao.NodeDAO;
import nl.tabuu.permissionshopz.dao.PerkDAO;
import nl.tabuu.permissionshopz.dao.ShopDAO;
import nl.tabuu.permissionshopz.data.Shop;
import nl.tabuu.permissionshopz.nodehandler.INodeHandler;
import nl.tabuu.permissionshopz.nodehandler.NodeHandler;
import nl.tabuu.permissionshopz.util.NumberFormat;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.file.JsonConfiguration;
import nl.tabuu.tabuucore.configuration.file.YamlConfiguration;
import nl.tabuu.tabuucore.plugin.TabuuCorePlugin;
import nl.tabuu.tabuucore.util.Dictionary;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public class PermissionShopZ extends TabuuCorePlugin {
    private static PermissionShopZ INSTANCE;

    private PerkDAO _perkDao;
    private NodeDAO _nodeDao;
    private ShopDAO _shopDao;

    private Shop _defaultShop;

    private Dictionary _locale;
    private IConfiguration _config;
    private NodeHandler _nodeHandler;

    @Override
    public void onEnable() {
        INSTANCE = this;

        getConfigurationManager().addConfiguration("nodes.json", JsonConfiguration.class);
        getConfigurationManager().addConfiguration("perks.json", JsonConfiguration.class);
        getConfigurationManager().addConfiguration("shops.json", JsonConfiguration.class);

        _nodeDao = new NodeDAO();
        _perkDao = new PerkDAO();
        _shopDao = new ShopDAO();

        load();

        registerExecutors(new PermissionShopCommand());

        Metrics metrics = new Metrics(this, 7110);
        Metrics.SimplePie handlerChart = new Metrics.SimplePie("permission_handler", _nodeHandler::getName);
        metrics.addCustomChart(handlerChart);

        getLogger().info("PermissionShopZ is now enabled.");
    }

    @Override
    public void onDisable() {
        unload();
        getLogger().info("PermissionShopZ is now disabled.");
    }

    public void unload() {
        _nodeDao.writeAll();
        _perkDao.writeAll();
        _shopDao.writeAll();
        _nodeHandler = null;
    }

    public void load() {
        getConfigurationManager().reloadAll();

        _config = getConfigurationManager().addConfiguration("config.yml", YamlConfiguration.class);
        _locale = getConfigurationManager().addConfiguration("lang.yml", YamlConfiguration.class).getDictionary("");

        _nodeDao.readAll();
        _perkDao.readAll();
        _shopDao.readAll();

        Optional<Shop> optionalShop = _shopDao.getAllFiltered(shop -> "Default".equals(shop.getName())).stream().findFirst();
        if(!optionalShop.isPresent()) {
            _defaultShop = new Shop("Default", "Description");
            _shopDao.create(_defaultShop);
        } else _defaultShop = optionalShop.get();

        getNodeHandler();
        NumberFormat.reloadSuffixMap();
    }

    public void reload() {
        unload();

        load();
    }

    public Dictionary getLocale() {
        return _locale;
    }

    public IConfiguration getConfiguration() {
        return _config;
    }

    public Shop getDefaultShop() {
        return _defaultShop;
    }

    public PerkDAO getPerkDao() {
        return _perkDao;
    }

    public NodeDAO getNodeDao() {
        return _nodeDao;
    }

    public ShopDAO getShopDao() {
        return _shopDao;
    }

    @Nonnull
    public INodeHandler getNodeHandler() {
        if(Objects.isNull(_nodeHandler)) {
            NodeHandler handler = _config.get("PermissionManager", NodeHandler::valueOf);

            assert Objects.nonNull(handler) : "No permission handler specified";
            _nodeHandler = handler;
        }

        return _nodeHandler.getHandler();
    }

    public static PermissionShopZ getInstance() {
        return INSTANCE;
    }
}