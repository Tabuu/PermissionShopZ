package nl.tabuu.permissionshopz.gui.element;

import nl.tabuu.permissionshopz.gui.FutureSupplierInventoryFormUI;
import nl.tabuu.tabuucore.inventory.ui.element.IClickable;
import nl.tabuu.tabuucore.inventory.ui.element.IValuable;
import nl.tabuu.tabuucore.inventory.ui.element.StyleableElement;
import nl.tabuu.tabuucore.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ListEditor<T> extends StyleableElement<ListEditorStyle> implements IClickable, IValuable<List<T>> {
    private final FutureSupplierInventoryFormUI<T> _supplier;

    private final List<BiConsumer<Player, T>> _onAdd;
    private final List<BiConsumer<Player, T>> _onRemove;
    private final List<BiConsumer<Player, List<T>>> _onChange;

    private List<T> _items;
    private int _selectedIndex = 0;

    public ListEditor(
            ListEditorStyle style,
            List<T> items,
            FutureSupplierInventoryFormUI<T> supplier) {
        super(style);
        _items = new ArrayList<>(items);
        _supplier = supplier;

        _onAdd = new LinkedList<>();
        _onRemove = new LinkedList<>();
        _onChange = new LinkedList<>();

        supplier.onSupply(this::add);
    }

    @Override
    public void click(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        switch (event.getClick()) {
            case NUMBER_KEY:
                _selectedIndex = event.getHotbarButton() % getValue().size();
                break;

            case MIDDLE:
                remove(player, _selectedIndex);
                _selectedIndex = 0;
                break;

            case RIGHT:
                _supplier.open(player);
                break;

            default:
                if (getValue().size() < 1) break;
                _selectedIndex = (_selectedIndex + 1) % getValue().size();
                break;
        }
    }

    protected void add(@Nonnull Player player, @Nonnull T object) {
        getValue().add(object);
        _onAdd.forEach(consumer -> consumer.accept(player, object));
        _onChange.forEach(consumer -> consumer.accept(player, getValue()));
    }

    protected void remove(@Nonnull Player player, int index) {
        if(index >= getValue().size()) return;
        T object = getValue().get(index);

        if(Objects.isNull(object)) return;
        remove(player, object);
    }

    protected void remove(@Nonnull Player player, @Nonnull T object) {
        if(getValue().remove(object)) {
            _onRemove.forEach(consumer -> consumer.accept(player, object));
            _onChange.forEach(consumer -> consumer.accept(player, getValue()));
        }
    }

    public ListEditor<T> onAdd(BiConsumer<Player, T> onAdd) {
        _onAdd.add(onAdd);
        return this;
    }

    public ListEditor<T> onRemove(BiConsumer<Player, T> onRemove) {
        _onRemove.add(onRemove);
        return this;
    }

    public ListEditor<T> onChange(BiConsumer<Player, List<T>> onChange) {
        _onChange.add(onChange);
        return this;
    }

    @Override
    public ItemStack getDisplayItem() {
        String[] lore = new String[getValue().size()];

        for (int i = 0; i < getValue().size(); i++) {
            T item = getValue().get(i);
            if (item == null) continue;

            lore[i] = getStyle().getDisplayString(item.toString(), _selectedIndex == i);
        }

        ItemBuilder builder = new ItemBuilder(super.getDisplayItem().clone());
        builder.addLore(lore);

        return builder.build();
    }

    @Override
    public List<T> getValue() {
        return _items;
    }

    @Override
    public void setValue(List<T> list) {
        _items = list;
    }
}