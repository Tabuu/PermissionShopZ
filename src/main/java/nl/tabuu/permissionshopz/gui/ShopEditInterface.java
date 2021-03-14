package nl.tabuu.permissionshopz.gui;

import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Objects;

public class ShopEditInterface extends ShopInterface {

    public ShopEditInterface(Player player) {
        super(player);
        setTitle(_local.translate("GUI_SHOP_EDITOR_TITLE", getReplacements()));
    }

    @Override
    protected void onPerkClick(Player player, Perk perk) {
        new PerkEditInterface(perk).open(player);
    }

    @Override
    protected void updatePage() {
        String raw = _local.getOrDefault("GUI_REMOVE_TITLE", "GUI_REMOVE_TITLE");
        if(Objects.nonNull(raw) && raw.contains("{PAGE}")) {
            setTitle(_local.translate("GUI_SHOP_EDITOR_TITLE", getReplacements()));
            reload();
        }
        onDraw();
    }

    @Override
    protected Button createPerkItem(Player player, Perk perk) {
        Button button = super.createPerkItem(player, perk);

        button.setEnabled(true);

        ItemMeta meta = button.getStyle().getEnabled().getItemMeta();
        if(meta == null) return button;

        meta.setLore(Collections.singletonList(_local.translate("GUI_SHOP_EDITOR_PERK_LORE")));
        button.getStyle().getEnabled().setItemMeta(meta);

        return button;
    }
}