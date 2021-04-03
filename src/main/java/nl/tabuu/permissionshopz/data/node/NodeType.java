package nl.tabuu.permissionshopz.data.node;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public enum NodeType {
    PERMISSION,
    TEMPORARY_PERMISSION,
    GROUP,
    TRACK,
    UNKNOWN;

    private ItemStack _icon;

    public ItemStack getIcon() {
        if (Objects.isNull(_icon)) {
            XMaterial material = PermissionShopZ.getInstance().getConfiguration().get("Icons.NodeTypes." + name(), XMaterial::valueOf);
            if (Objects.isNull(material)) material = XMaterial.BARRIER;

            _icon = new ItemBuilder(material)
                    .setDisplayName(toString())
                    .build();
        }

        return _icon;
    }

    @Override
    public String toString() {
        String locale = String.format("NODE_TYPE_%s", name().toUpperCase());
        return PermissionShopZ.getInstance().getLocale().translate(locale);
    }
}