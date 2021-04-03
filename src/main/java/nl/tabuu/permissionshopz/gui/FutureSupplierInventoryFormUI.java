package nl.tabuu.permissionshopz.gui;

import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryFormUI;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class FutureSupplierInventoryFormUI<T> extends InventoryFormUI {
    private final InventoryUI _returnUI;
    private final List<BiConsumer<Player, T>> _callbacks;

    protected FutureSupplierInventoryFormUI(String title, InventorySize size, InventoryUI returnUI) {
        super(title, size);
        _returnUI = returnUI;
        _callbacks = new LinkedList<>();
    }

    public void onSupply(BiConsumer<Player, T> onSupply) {
        _callbacks.add(onSupply);
    }

    protected void supply(Player player, T object) {
        for(BiConsumer<Player, T> callback : _callbacks)
            callback.accept(player, object);
        _returnUI.open(player);
    }
}