package nl.tabuu.permissionshopz.gui.element;

import nl.tabuu.tabuucore.inventory.ui.element.IClickable;
import nl.tabuu.tabuucore.inventory.ui.element.IValuable;
import nl.tabuu.tabuucore.inventory.ui.element.StyleableElement;
import nl.tabuu.tabuucore.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class EnumSelector<T extends Enum> extends StyleableElement<EnumSelectorStyle<T>> implements IClickable, IValuable<T> {
    private final T[] _values;
    private final List<BiConsumer<Player, T>> _onSelectionChange;

    private T _value;
    private int _selectedIndex;

    public EnumSelector(EnumSelectorStyle<T> style) {
        super(style);
        _values = findValues();
        _onSelectionChange = new LinkedList<>();

        _selectedIndex = 0;
    }

    public EnumSelector<T> onSelectionChange(BiConsumer<Player, T> onSelectionChange) {
        _onSelectionChange.add(onSelectionChange);
        return this;
    }

    // This is kind of dirty, but kind of needed.
    @SuppressWarnings("unchecked")
    private T[] findValues() {
        ParameterizedType parameterTypes = (ParameterizedType) getClass().getGenericSuperclass();
        Class<T> enumClass = (Class<T>) parameterTypes.getActualTypeArguments()[0];

        return enumClass.getEnumConstants();
    }

    @Override
    public ItemStack getDisplayItem() {
        String[] lore = new String[_values.length];

        for (int i = 0; i < _values.length; i++) {
            T item = _values[i];
            if (Objects.isNull(item)) continue;

            lore[i] = getStyle().getDisplayString(item.toString(), _selectedIndex == i);
        }

        ItemBuilder builder = new ItemBuilder(getStyle().getDisplayItem(getValue()));
        builder.addLore(lore);

        return builder.build();
    }

    @Override
    public void click(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getClick() == ClickType.NUMBER_KEY) {
            setSelectedIndex(player, event.getHotbarButton() % _values.length);
        } else {
            if (_values.length < 1) return;
            setSelectedIndex(player, (_selectedIndex + 1) % _values.length);
        }
    }

    public void setSelectedIndex(Player player, int index) {
        _selectedIndex = index;
        if(_values.length <= index) return;

        T value = _values[index];
        setValue(value);
        _onSelectionChange.forEach(consumer -> consumer.accept(player, value));
    }

    @Override
    public T getValue() {
        return _value;
    }

    @Override
    public void setValue(T value) {
        _value = value;
    }
}