package nl.tabuu.permissionshopz.gui;

import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import nl.tabuu.tabuucore.inventory.ui.TextInputUI;
import nl.tabuu.tabuucore.inventory.ui.element.IClickable;
import nl.tabuu.tabuucore.inventory.ui.element.IValuable;
import nl.tabuu.tabuucore.inventory.ui.element.StyleableElement;
import nl.tabuu.tabuucore.inventory.ui.element.style.TextInputStyle;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.serialization.IObjectDeserializer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public class ListEditor<V> extends StyleableElement<ListEditorStyle> implements IClickable, IValuable<List<V>> {

    private final InventoryUI _returnUI;
    private final TextInputStyle _inputStyle;
    private final BiConsumer<Player, List<V>> _onListChange;
    private final IObjectDeserializer<String, V> _deserializer;

    private List<V> _items;
    private int _selectedIndex = 0;

    public ListEditor(
            ListEditorStyle style,
            TextInputStyle inputStyle,
            InventoryUI returnUI, List<V> items,
            IObjectDeserializer<String, V> deserializer,
            BiConsumer<Player, List<V>> onListChange) {
        super(style);
        _items = items;
        _returnUI = returnUI;
        _inputStyle = inputStyle;
        _deserializer = deserializer;
        _onListChange = onListChange;
    }

    @Override
    public void click(InventoryClickEvent event) {
        switch (event.getClick()) {

            case NUMBER_KEY:
                _selectedIndex = event.getHotbarButton() % getValue().size();
                break;

            case MIDDLE:
                if(!getValue().isEmpty()) getValue().remove(_selectedIndex);
                _selectedIndex = 0;
                break;

            case RIGHT:
                ItemStack item = _inputStyle.getRenameItem();
                Player player = (Player) event.getWhoClicked();
                String placeholder = _inputStyle.getPlaceHolder();

                new TextInputUI(item, placeholder, this::addItem, this::returnToUI).open(player);
                break;

            default:
                if(getValue().size() < 1) break;
                _selectedIndex = (_selectedIndex + 1) % getValue().size();
                break;
        }
    }

    private void returnToUI(Player player) {
        _returnUI.open(player);
    }

    @Override
    public ItemStack getDisplayItem() {
        String[] lore = new String[getValue().size()];

        for(int i = 0; i < getValue().size(); i++) {
            V item = getValue().get(i);
            if(item == null) continue;

            String
                    itemString = item.toString(),
                    entry = getStyle().getEntry(),
                    replacement = getStyle().getReplacement(),
                    selectedEntry = getStyle().getSelectedEntry(),
                    line = _selectedIndex == i ? selectedEntry : entry;

            line = line.replace(replacement, itemString);
            lore[i] = line;
        }

        ItemBuilder builder = new ItemBuilder(super.getDisplayItem().clone());
        builder.addLore(lore);

        return builder.build();
    }

    private void addItem(Player player, String string) {
        V item = _deserializer.deserialize(string);
        if(item != null) getValue().add(item);

        _onListChange.accept(player, getValue());
        returnToUI(player);
    }

    @Override
    public List<V> getValue() {
        return _items;
    }

    @Override
    public void setValue(List<V> list) {
        _items = list;
    }
}