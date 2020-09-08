package nl.tabuu.permissionshopz.gui;

import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.material.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ListEditorStyle extends Style {

    private String _entry, _selectedEntry, _replacement;

    public ListEditorStyle(ItemStack enabled, ItemStack disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled);
        _entry = entry;
        _selectedEntry = selectedEntry;
        _replacement = replacement;
    }

    public ListEditorStyle(ItemStack display, String entry, String selectedEntry, String replacement) {
        super(display);
        _entry = entry;
        _selectedEntry = selectedEntry;
        _replacement = replacement;
    }

    public ListEditorStyle(Material enabled, Material disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled);
        _entry = entry;
        _selectedEntry = selectedEntry;
        _replacement = replacement;
    }

    public ListEditorStyle(Material display, String entry, String selectedEntry, String replacement) {
        super(display);
        _entry = entry;
        _selectedEntry = selectedEntry;
        _replacement = replacement;
    }

    public ListEditorStyle(XMaterial enabled, XMaterial disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled);
        _entry = entry;
        _selectedEntry = selectedEntry;
        _replacement = replacement;
    }

    public ListEditorStyle(XMaterial display, String entry, String selectedEntry, String replacement) {
        super(display);
        _entry = entry;
        _selectedEntry = selectedEntry;
        _replacement = replacement;
    }

    @Override
    public ItemStack getEnabled() {
        return super.getEnabled();
    }

    @Override
    public ItemStack getDisabled() {
        return super.getDisabled();
    }

    public String getEntry() {
        return _entry;
    }

    public String getSelectedEntry() {
        return _selectedEntry;
    }

    public String getReplacement() {
        return _replacement;
    }
}
