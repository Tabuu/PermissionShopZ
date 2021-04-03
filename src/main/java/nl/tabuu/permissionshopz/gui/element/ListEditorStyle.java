package nl.tabuu.permissionshopz.gui.element;

import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.material.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ListEditorStyle extends Style {

    private final String _entry, _selectedEntry, _replacement;

    public ListEditorStyle(ItemStack enabled, ItemStack disabled, String entry, String selectedEntry, String replacement) {
        super(enabled, disabled);
        _entry = entry;
        _selectedEntry = selectedEntry;
        _replacement = replacement;
    }

    public ListEditorStyle(ItemStack display, String entry, String selectedEntry, String replacement) {
        this(display, display, entry, selectedEntry, replacement);
    }

    public ListEditorStyle(Material enabled, Material disabled, String entry, String selectedEntry, String replacement) {
        this(new ItemStack(enabled), new ItemStack(disabled), entry, selectedEntry, replacement);
    }

    public ListEditorStyle(Material display, String entry, String selectedEntry, String replacement) {
        this(display, display, entry, selectedEntry, replacement);
    }

    public ListEditorStyle(XMaterial enabled, XMaterial disabled, String entry, String selectedEntry, String replacement) {
        this(enabled.parseItem(), disabled.parseItem(), entry, selectedEntry, replacement);
    }

    public ListEditorStyle(XMaterial display, String entry, String selectedEntry, String replacement) {
        this(display, display, entry, selectedEntry, replacement);
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

    public String getDisplayString(String entry, boolean selected) {
        if(Objects.isNull(entry)) return null;

        String
                entryTemplate = getEntry(),
                replacement = getReplacement(),
                selectedEntry = getSelectedEntry(),
                line = selected ? selectedEntry : entryTemplate;

        line = line.replace(replacement, entry);
        return line;
    }
}