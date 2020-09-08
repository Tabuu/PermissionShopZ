package nl.tabuu.permissionshopz.gui;

import nl.tabuu.tabuucore.debug.Debug;
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
import java.util.function.Consumer;

public class ListEditor<V> extends StyleableElement<ListEditorStyle> implements IClickable, IValuable<List<V>> {

    private int _index = 0;
    private List<V> _items;
    private IObjectDeserializer<String, V> _deserializer;
    private InventoryUI _returnUI;
    private TextInputStyle _inputStyle;
    private BiConsumer<Player, List<V>> _onListChange;

    public ListEditor(ListEditorStyle style, TextInputStyle inputStyle, InventoryUI returnUI, List<V> items, IObjectDeserializer<String, V> deserializer, BiConsumer<Player, List<V>> onListChange) {
        super(style);
        _returnUI = returnUI;
        _items = items;
        _deserializer = deserializer;
        _inputStyle = inputStyle;
        _onListChange = onListChange;
    }

    @Override
    public void click(InventoryClickEvent event) {
        if(event.isShiftClick()) {
            if(!_items.isEmpty()) _items.remove(_index);
            _index = 0;
        } else if (event.isLeftClick()) {
            _index = (_index + 1) % _items.size();
        } else if (event.isRightClick()) {
            new TextInputUI(_inputStyle.getRenameItem(), _inputStyle.getPlaceHolder(), this::addItem, this::returnToUI).open(event.getWhoClicked());
        }
    }

    private void addItem(Player player, String string) {
        V item = _deserializer.deserialize(string);
        if(item != null) _items.add(item);
        _onListChange.accept(player, getValue());
        returnToUI(player);
    }

    private void returnToUI(Player player) {
        _returnUI.open(player);
    }

    @Override
    public ItemStack getDisplayItem() {
        String[] lore = new String[_items.size()];

        for(int i = 0; i < _items.size(); i++) {
            V item = _items.get(i);
            if(item == null) continue;

            String string = item.toString();
            String entry = i == _index ? getStyle().getSelectedEntry() : getStyle().getEntry();
            entry = entry.replace(getStyle().getReplacement(), string);
            lore[i] = entry;
        }

        ItemBuilder builder = new ItemBuilder(super.getDisplayItem().clone());
        builder.addLore(lore);

        return builder.build();
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
