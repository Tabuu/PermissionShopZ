package nl.tabuu.permissionshopz.gui;

import nl.tabuu.permissionshopz.permissionhandler.NodeType;
import nl.tabuu.tabuucore.material.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ListEditorNodeStyle extends ListEditorStyle{
    public ListEditorNodeStyle(ItemStack enabled, ItemStack disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled, entry, selectedEntry, replacement);
    }

    public ListEditorNodeStyle(ItemStack display, String entry, String selectedEntry, String replacement) {
        super(display, entry, selectedEntry, replacement);
    }

    public ListEditorNodeStyle(Material enabled, Material disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled, entry, selectedEntry, replacement);
    }

    public ListEditorNodeStyle(Material display, String entry, String selectedEntry, String replacement) {
        super(display, entry, selectedEntry, replacement);
    }

    public ListEditorNodeStyle(XMaterial enabled, XMaterial disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled, entry, selectedEntry, replacement);
    }

    public ListEditorNodeStyle(XMaterial display, String entry, String selectedEntry, String replacement) {
        super(display, entry, selectedEntry, replacement);
    }

    @Override
    public String getDisplayString(String entry, boolean selected) {
        NodeType nodeType = NodeType.fromNode(entry);
        return super.getDisplayString(nodeType.toString(entry), selected);
    }
}
