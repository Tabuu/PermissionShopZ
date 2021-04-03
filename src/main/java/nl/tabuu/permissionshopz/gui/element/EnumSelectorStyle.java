package nl.tabuu.permissionshopz.gui.element;

import nl.tabuu.tabuucore.material.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class EnumSelectorStyle<T extends Enum> extends ListEditorStyle {
    public EnumSelectorStyle(ItemStack enabled, ItemStack disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled, entry, selectedEntry, replacement);
    }

    public EnumSelectorStyle(ItemStack display, String entry, String selectedEntry, String replacement) {
        super(display, entry, selectedEntry, replacement);
    }

    public EnumSelectorStyle(Material enabled, Material disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled, entry, selectedEntry, replacement);
    }

    public EnumSelectorStyle(Material display, String entry, String selectedEntry, String replacement) {
        super(display, entry, selectedEntry, replacement);
    }

    public EnumSelectorStyle(XMaterial enabled, XMaterial disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled, entry, selectedEntry, replacement);
    }

    public EnumSelectorStyle(XMaterial display, String entry, String selectedEntry, String replacement) {
        super(display, entry, selectedEntry, replacement);
    }

    public abstract ItemStack getDisplayItem(T value);
}