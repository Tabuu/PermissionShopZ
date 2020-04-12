package nl.tabuu.permissionshopz.gui;

import net.milkbowl.vault.economy.Economy;
import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.util.Message;
import nl.tabuu.tabuucore.configuration.ConfigurationManager;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.economy.hook.Vault;
import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryFormUI;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.inventory.ui.graphics.brush.CheckerBrush;
import nl.tabuu.tabuucore.inventory.ui.graphics.brush.IBrush;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.util.Dictionary;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopInterface extends InventoryFormUI {

    private Economy _economy;
    protected Dictionary _local;
    private IConfiguration _config;

    private int _page;
    private int _maxPage;
    private Player _player;
    protected List<Perk> _perks;

    public ShopInterface(Player player) {
        super("", InventorySize.THREE_ROWS);
        ConfigurationManager manager = PermissionShopZ.getInstance().getConfigurationManager();

        _economy = Vault.getEconomy();
        _config = manager.getConfiguration("config");
        _local = manager.getConfiguration("lang").getDictionary("");

        InventorySize size = _config.getEnum(InventorySize.class, "GUISize");
        if(size != null && size.getHeight() >= 3) setSize(size);

        _player = player;
        _perks = new ArrayList<>(PermissionShopZ.getInstance().getPerkManager().getPerks());

        int contentWidth = getSize().getWidth() - 2;
        int contentHeight = getSize().getHeight() - 2;
        _maxPage = _perks.size() / (contentWidth * contentHeight);

        setTitle(_local.translate("GUI_TITLE", "{PAGE}", (getPage() + 1) + ""));
        reload();
    }

    @Override
    protected void draw() {
        ItemBuilder
                next = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                        .setDisplayName(_local.translate("GUI_PAGE_NEXT")),

                previous = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                        .setDisplayName(_local.translate("GUI_PAGE_PREVIOUS")),

                barrier = new ItemBuilder(XMaterial.BARRIER)
                        .setDisplayName(_local.translate("GUI_PAGE_CLOSE")),

                black = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE)
                        .setDisplayName(" "),

                gray = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                        .setDisplayName(" ");


        Style
                nextButtonStyle = new Style(next.build(), next.build()),
                previousButtonStyle = new Style(previous.build(), previous.build()),
                exitButtonStyle = new Style(barrier.build(), barrier.build()),
                clearButtonStyle = new Style(XMaterial.AIR, XMaterial.AIR);

        Button
                nextButton = new Button(nextButtonStyle, this::nextPage),
                previousButton = new Button(previousButtonStyle, this::previousPage),
                exitButton = new Button(exitButtonStyle, this::close),
                clearButton = new Button(clearButtonStyle);

        IBrush brush = new CheckerBrush(black.build(), gray.build());
        setBrush(brush);

        Vector2f borderStart = new Vector2f(0, 0);
        Vector2f borderStop = new Vector2f(getSize().getWidth() - 1, getSize().getHeight() - 1);
        drawRectangle(borderStart, borderStop);

        setElement(new Vector2f(borderStop.getX(), borderStop.getY()), nextButton);
        setElement(new Vector2f(borderStart.getX(), borderStop.getY()), previousButton);
        setElement(new Vector2f(borderStop.getX() / 2, borderStop.getY()), exitButton);

        int rowSize = getSize().getWidth() - 2;
        int maxRows = getSize().getHeight() - 2;
        Vector2f offset = new Vector2f(1, 1);

        for(int i = 0; i < rowSize * maxRows; i++) {
            int index = getPage() * (rowSize * maxRows) + i;
            int x = i % rowSize;
            int y = i / rowSize;

            Vector2f position = new Vector2f(x, y).add(offset);

            if(index < _perks.size()) {
                Perk perk = _perks.get(index);
                Button button = createPerkItem(_player, perk);
                setElement(position, button);
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
        String message;
        if (_economy.has(player, perk.getCost())) {
            _economy.withdrawPlayer(player, perk.getCost());
            perk.apply(player);
            message = "PERK_BUY_SUCCESS";
        }
        else message = "ERROR_INSUFFICIENT_FUNDS";

        message = _local.translate(message, perk.getReplacements());
        Message.send(player, message);
        updatePage();
    }

    protected void updatePage() {
        String raw = _local.get("GUI_TITLE");
        if(raw.contains("{PAGE}")) {
            setTitle(_local.translate("GUI_TITLE", "{PAGE}", (getPage() + 1) + ""));
            reload();
        }
        draw();
    }

    public int getPage() {
        return _page;
    }

    private void nextPage(Player player) {
        if (_page < _maxPage) _page++;

        updatePage();
    }

    private void previousPage(Player player) {
        if (_page > 0) _page--;

        updatePage();
    }
}
