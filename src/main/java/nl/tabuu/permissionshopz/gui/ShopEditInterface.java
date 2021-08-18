package nl.tabuu.permissionshopz.gui;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.Shop;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShopEditInterface extends ShopInterface {

    public ShopEditInterface(Shop shop, Player player) {
        super(shop, player);
        setTitle(_local.translate("GUI_FORM_SHOP_EDITOR_TITLE", getReplacements()));
    }

    @Override
    protected void onDraw() {
        ItemBuilder addButtonItem = new ItemBuilder(XMaterial.EMERALD)
                .setDisplayName(_local.translate("GUI_FORM_SHOP_EDITOR_ADD"));

        Style addButtonStyle = new Style(addButtonItem);
        Button addButton = new Button(addButtonStyle, this::onAddButtonClick);

        setElement(new Vector2f(4, 0), addButton);

        super.onDraw();
    }

    @Override
    protected void onPerkClick(Player player, Perk perk) {
        new PerkEditInterface(getShop(), perk).open(player);
    }

    @Override
    protected void updatePage() {
        String raw = _local.getOrDefault("GUI_FORM_SHOP_EDITOR_TITLE", "GUI_FORM_SHOP_EDITOR_TITLE");
        if (Objects.nonNull(raw) && raw.contains("{CURRENT}")) {
            setTitle(_local.translate("GUI_FORM_SHOP_EDITOR_TITLE", getReplacements()));
            reload();
        }
        onDraw();
    }

    @Override
    protected boolean shouldDisplay(Player player, Perk perk) {
        return true;
    }

    @Nonnull
    @Override
    protected Button createPerkItem(Player player, Perk perk) {
        ItemBuilder displayItemBuilder = new ItemBuilder(perk.getDisplayItem());

        String displayName = _local.translate("PERK_TITLE", perk.getReplacements());
        displayItemBuilder.setDisplayName(displayName);

        String awardedNodeString = perk.getAwardedNodes().stream().map(node -> {
            String entryKey = _permission.hasNode(getPlayer(), node) ? "PERK_AWARDED_NODE_ENTRY_HAS" : "PERK_AWARDED_NODE_ENTRY";
            return _local.translate(entryKey, "{NODE}", node);
        }).collect(Collectors.joining("\n"));

        String requiredNodeString = perk.getRequiredNodes().stream().map(node -> {
            String entryKey = _permission.hasNode(getPlayer(), node) ? "PERK_REQUIRED_NODE_ENTRY_HAS" : "PERK_REQUIRED_NODE_ENTRY";
            return _local.translate(entryKey, "{NODE}", node);
        }).collect(Collectors.joining("\n"));

        String footer = _local.translate("GUI_FORM_SHOP_EDITOR_PERK_FOOTER", perk.getReplacements());

        String lore = _local.translate("PERK_LORE", "{AWARDED_NODES}", awardedNodeString, "{REQUIRED_NODES}", requiredNodeString, "{FOOTER}", footer);

        displayItemBuilder.setLore(lore);

        Style style = new Style(displayItemBuilder);
        return new Button(style, p -> onPerkClick(player, perk));
    }

    private void onAddButtonClick(Player player) {
        Perk perk = new Perk();
        PerkEditInterface edit = new PerkEditInterface(getShop(), perk);
        edit.open(player);

        PermissionShopZ.getInstance().getPerkDao().create(perk);
        getShop().add(perk);
    }
}