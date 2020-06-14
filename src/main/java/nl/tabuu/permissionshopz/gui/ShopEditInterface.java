package nl.tabuu.permissionshopz.gui;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.PerkManager;
import nl.tabuu.permissionshopz.util.Message;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ShopEditInterface extends ShopInterface {
    public ShopEditInterface(Player player) {
        super(player);

        setTitle(_local.translate("GUI_REMOVE_TITLE", getReplacements()));
    }

    @Override
    protected void onPerkClick(Player player, Perk perk) {
        PerkManager manager = PermissionShopZ.getInstance().getPerkManager();
        manager.removePerk(perk.getUniqueId());
        _perks.remove(perk);
        Message.send(player, _local.translate("PERK_REMOVE_SUCCESS", "{NAME}", perk.getName()));
        updatePage();
    }

    @Override
    protected void updatePage() {
        String raw = _local.get("GUI_REMOVE_TITLE");
        if(raw.contains("{PAGE}")) {
            setTitle(_local.translate("GUI_REMOVE_TITLE", getReplacements()));
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

        meta.setLore(Collections.singletonList(_local.translate("GUI_REMOVE_CLICK")));
        button.getStyle().getEnabled().setItemMeta(meta);

        return button;
    }
}
