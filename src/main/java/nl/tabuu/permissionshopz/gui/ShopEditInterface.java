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
    }

    @Override
    protected void onPerkClick(Player player, Perk perk) {
        PerkManager manager = PermissionShopZ.getInstance().getPerkManager();
        manager.removePerk(perk.getUniqueId());

        Message.send(player, _local.translate("PERK_REMOVE_SUCCESS", "{PERK_NAME}", perk.getName()));
        updateTitle();
    }

    @Override
    protected void updateTitle() {
        setTitle(_local.translate("GUI_REMOVE_TITLE", "{PAGE_NUMBER}", (_currentPage + 1) + ""));
        reload();
        draw();
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
