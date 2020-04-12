package nl.tabuu.permissionshopz.gui;

import net.milkbowl.vault.economy.Economy;
import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.permissionhandler.IPermissionHandler;
import nl.tabuu.permissionshopz.util.Message;
import nl.tabuu.permissionshopz.util.NumberFormat;
import nl.tabuu.tabuucore.configuration.ConfigurationManager;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.economy.hook.Vault;
import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryFormUI;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.util.BukkitUtils;
import nl.tabuu.tabuucore.util.Dictionary;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopInterface extends InventoryFormUI {

    private Economy _economy;
    protected Dictionary _local;
    private IConfiguration _config;

    private Player _player;
    private IPermissionHandler _permissionHandler;

    private int _maxPage;
    private int _page = 0;
    protected List<Perk> _perks;

    public ShopInterface(Player player) {
        super("", InventorySize.TWO_ROWS);
        ConfigurationManager manager = PermissionShopZ.getInstance().getConfigurationManager();

        _economy = Vault.getEconomy();
        _config = manager.getConfiguration("config");
        _local = manager.getConfiguration("lang").getDictionary("");

        _player = player;
        _permissionHandler = PermissionShopZ.getInstance().getPermissionHandler();

        _maxPage = PermissionShopZ.getInstance().getPerkManager().getPerks().size() / 9;
        _perks = new ArrayList<>(PermissionShopZ.getInstance().getPerkManager().getPerks());

        updateTitle();
    }

    @Override
    protected void draw() {
        ItemBuilder
                next = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                        .setDisplayName(_local.translate("GUI_PAGE_NEXT")),

                previous = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                        .setDisplayName(_local.translate("GUI_PAGE_PREVIOUS")),

                barrier = new ItemBuilder(XMaterial.BARRIER)
                        .setDisplayName(_local.translate("GUI_PAGE_CLOSE"));

        Style
                nextButtonStyle = new Style(next.build(), next.build()),
                previousButtonStyle = new Style(previous.build(), previous.build()),
                exitButtonStyle = new Style(barrier.build(), barrier.build()),
                clearButtonStyle = new Style(XMaterial.AIR, XMaterial.AIR);

        Button
                nextButton = new Button(nextButtonStyle, this::nextPage),
                previousButton = new Button(previousButtonStyle, this::previousPage),
                exitButton = new Button(exitButtonStyle, this::onCloseButton),
                clearButton = new Button(clearButtonStyle);

        setElement(new Vector2f(8, 1), nextButton);
        setElement(new Vector2f(0, 1), previousButton);
        setElement(new Vector2f(4, 1), exitButton);

        for (int i = _page * 9; i < (_page * 9) + 9; i++) {
            Vector2f position = new Vector2f(i % 9, 0);

            if(i < _perks.size()) {
                Perk perk = _perks.get(i);
                setElement(position, createPerkItem(_player, perk));
            }
            else setElement(position, clearButton);
        }
        super.draw();
    }

    protected Button createPerkItem(Player player, Perk perk) {
        XMaterial unlockedMaterial = _config.getEnum(XMaterial.class, "UnlockedMaterial");
        boolean unlocked = perk.getPermissions().stream().allMatch(node -> _player.hasPermission(node));

        ItemStack displayItem;
        if(unlocked && unlockedMaterial != null) displayItem = unlockedMaterial.parseItem();
        else if(perk.getDisplayItem() != null) displayItem = perk.getDisplayItem();
        else displayItem = XMaterial.BARRIER.parseItem();

        assert displayItem != null;

        ItemBuilder displayItemBuilder = new ItemBuilder(displayItem);

        String displayName = _local.translate("GUI_PERK_TITLE", perk.getReplacements());
        displayItemBuilder.setDisplayName(displayName);

        if (_config.getBoolean("DisplayPermissionList")) {
            for (String node : perk.getPermissions()) {
                String text = _player.hasPermission(node) ? "GUI_PERK_NODE_HAS" : "GUI_PERK_NODE";
                text = _local.translate(text, "{NODE}", node);
                displayItemBuilder.addLore(text);
            }
        }

        String footer = unlocked ? "GUI_PERK_UNLOCKED_FOOTER" : "GUI_PERK_LOCKED_FOOTER";
        footer = _local.translate(footer, perk.getReplacements());
        displayItemBuilder.addLore(footer);

        Style style = new Style(displayItemBuilder.build(), displayItemBuilder.build());
        Button button = new Button(style, p -> onPerkClick(player, perk));
        button.setEnabled(!unlocked);

        return button;
    }

    protected void onPerkClick(Player player, Perk perk) {
        if (_economy.has(player, perk.getCost())) {
            _economy.withdrawPlayer(player, perk.getCost());
            perk.getPermissions().forEach(node -> _permissionHandler.addPermission(player, node));
            Message.send(player, _local.translate("PERK_BUY_SUCCESS", perk.getReplacements()));
        } else {
            Message.send(player, _local.translate("ERROR_INSUFFICIENT_FUNDS", perk.getReplacements()));
        }
        updateTitle();
    }

    protected void updateTitle() {
        setTitle(_local.translate("GUI_TITLE", "{PAGE}", (_page + 1) + ""));
        reload();
        draw();
    }

    private void onCloseButton(Player player) {
        this.close(player);
    }

    public int getPage() {
        return _page;
    }

    private void nextPage(Player player) {
        if (_page < _maxPage)
            _page++;

        updateTitle();
    }

    private void previousPage(Player player) {
        if (_page > 0)
            _page--;

        updateTitle();
    }
}
