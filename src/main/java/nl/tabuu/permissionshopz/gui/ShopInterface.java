package nl.tabuu.permissionshopz.gui;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.Shop;
import nl.tabuu.permissionshopz.nodehandler.INodeHandler;
import nl.tabuu.permissionshopz.util.Message;
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

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class ShopInterface extends InventoryFormUI {
    protected final Economy _economy;
    protected final Dictionary _local;
    protected final IConfiguration _config;
    protected final INodeHandler _permission;

    private int _page;
    private final Shop _shop;
    private final int _maxPage;
    private final Player _player;
    private final List<Perk> _perks;

    public ShopInterface(Shop shop, Player player) {
        super("", InventorySize.THREE_ROWS);

        _economy = Vault.getEconomy();
        _config = PermissionShopZ.getInstance().getConfiguration();
        _local = PermissionShopZ.getInstance().getLocale();
        _permission = PermissionShopZ.getInstance().getNodeHandler();

        InventorySize size = _config.get("GUISize", InventorySize::valueOf);
        if (size != null && size.getHeight() >= 3) setSize(size);

        _shop = shop;
        _player = player;
        _perks = shop.getContents().stream()
                .filter(perk -> shouldDisplay(getPlayer(), perk))
                .collect(ArrayList::new, List::add, List::addAll);

        int contentWidth = getSize().getWidth() - 2;
        int contentHeight = getSize().getHeight() - 2;
        _maxPage = _perks.size() / (contentWidth * contentHeight);

        setTitle(_local.translate("GUI_FORM_SHOP_TITLE", getReplacements()));
    }

    @Override
    protected void onDraw() {
        ItemBuilder
                next = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setDisplayName(_local.translate("GUI_NAVIGATION_NEXT", getReplacements())),

                previous = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                        .setDisplayName(_local.translate("GUI_NAVIGATION_PREVIOUS", getReplacements())),

                barrier = new ItemBuilder(XMaterial.BARRIER)
                        .setDisplayName(_local.translate("GUI_NAVIGATION_RETURN", getReplacements())),

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

        int width = getSize().getWidth() - 2;
        int height = getSize().getHeight() - 2;
        Vector2f offset = new Vector2f(1, 1);

        for (int i = 0; i < width * height; i++) {
            int index = getPage() * (width * height) + i;
            int x = i % width;
            int y = i / width;

            Vector2f position = new Vector2f(x, y).add(offset);

            if (index < _perks.size()) {
                Perk perk = _perks.get(index);
                Button button = createPerkItem(_player, perk);
                setElement(position, button);
            } else setElement(position, clearButton);
        }
        super.onDraw();
    }

    protected boolean shouldDisplay(Player player, Perk perk) {
        if (!getShop().displayUnavailablePerks()) {
            if (!_economy.has(player, perk.getCost())) return false;
            if (!perk.hasRequiredNodes(player)) return false;
        }

        boolean unlocked = perk.getAwardedNodes().stream().allMatch(node -> _permission.hasNode(_player, node));

        if (!getShop().displayUnlockedPerks()) {
            if (unlocked) return false;
        }

        return true;
    }

    @Nonnull
    protected Button createPerkItem(Player player, Perk perk) {
        XMaterial unlockedMaterial = _config.get("Icons.UnlockedMaterial", XMaterial::valueOf);

        boolean unlocked = perk.getAwardedNodes().stream().allMatch(node -> _permission.hasNode(_player, node));

        ItemBuilder displayItemBuilder = new ItemBuilder(perk.getDisplayItem());
        ItemBuilder unlockedDisplayItemBuilder = Objects.nonNull(unlockedMaterial) ? new ItemBuilder(unlockedMaterial) : new ItemBuilder(perk.getDisplayItem());

        String displayName = _local.translate("PERK_TITLE", perk.getReplacements());
        displayItemBuilder.setDisplayName(displayName);
        unlockedDisplayItemBuilder.setDisplayName(displayName);

        String awardedNodeString = perk.getAwardedNodes().stream().map(node -> {
            String entryKey = _permission.hasNode(_player, node) ? "PERK_AWARDED_NODE_ENTRY_HAS" : "PERK_AWARDED_NODE_ENTRY";
            return _local.translate(entryKey, "{NODE}", node);
        }).collect(Collectors.joining("\n"));

        String requiredNodeString = perk.getRequiredNodes().stream().map(node -> {
            String entryKey = _permission.hasNode(_player, node) ? "PERK_REQUIRED_NODE_ENTRY_HAS" : "PERK_REQUIRED_NODE_ENTRY";
            return _local.translate(entryKey, "{NODE}", node);
        }).collect(Collectors.joining("\n"));

        String footer = _local.translate(unlocked ? "PERK_FOOTER_UNLOCKED" : "PERK_FOOTER_LOCKED", perk.getReplacements());

        String lore = _local.translate("PERK_LORE", "{AWARDED_NODES}", awardedNodeString, "{REQUIRED_NODES}", requiredNodeString, "{FOOTER}", footer);

        displayItemBuilder.setLore(lore);
        unlockedDisplayItemBuilder.setLore(lore);

        Style style = new Style(displayItemBuilder, unlockedDisplayItemBuilder);
        Button button = new Button(style, p -> onPerkClick(player, perk));
        button.setEnabled(!unlocked);

        return button;
    }

    protected void onPerkClick(Player player, Perk perk) {

        if (perk.hasRequiredNodes(player)) {
            EconomyResponse response = _economy.withdrawPlayer(player, perk.getCost());

            if (response.type.equals(EconomyResponse.ResponseType.SUCCESS)) {
                perk.apply(player);
                Message.send(player, _local.translate("INFO_PERK_BUY", perk.getReplacements()));
            } else Message.send(player, _local.translate("INFO_ERROR_INSUFFICIENT_FUNDS", perk.getReplacements()));

        } else Message.send(player, _local.translate("INFO_ERROR_INSUFFICIENT_PERMISSION", perk.getReplacements()));

        updatePage();
    }

    protected void updatePage() {
        String raw = _local.getOrDefault("GUI_FORM_SHOP_TITLE", "GUI_FORM_SHOP_TITLE");
        if (Objects.nonNull(raw) && raw.contains("{CURRENT}")) {
            setTitle(_local.translate("GUI_FORM_SHOP_TITLE", getReplacements()));
            reload();
        }
        onDraw();
    }

    protected void nextPage(Player player) {
        if (getPage() < getMaxPage()) {
            _page++;
            updatePage();
        }
    }

    protected void previousPage(Player player) {
        if (getPage() > 0) {
            _page--;
            updatePage();
        }
    }

    protected Player getPlayer() {
        return _player;
    }

    protected Shop getShop() {
        return _shop;
    }

    protected Object[] getReplacements() {
        return new Object[]{
                "{CURRENT}", getPage() + 1,
                "{MAX}", _maxPage + 1,
                "{PLAYER}", _player.getName()
        };
    }

    public int getPage() {
        return _page;
    }

    public int getMaxPage() {
        return _maxPage;
    }
}