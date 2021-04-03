package nl.tabuu.permissionshopz.gui.nodeform;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.gui.FutureSupplierInventoryFormUI;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.util.Dictionary;

public abstract class NodeBuilderFormUI<T extends Node.Builder> extends FutureSupplierInventoryFormUI<Node> {

    private final T _builder;
    protected final Dictionary _locale;
    protected final IConfiguration _config;

    protected NodeBuilderFormUI(T builder, InventoryUI returnUI) {
        super("", InventorySize.ONE_BY_FIVE, returnUI);
        _builder = builder;
        _locale = PermissionShopZ.getInstance().getLocale();
        _config = PermissionShopZ.getInstance().getConfiguration();
    }

    @Override
    protected void onDraw() {

        ItemBuilder
                paper = new ItemBuilder(XMaterial.PAPER)
                    .setDisplayName(_locale.translate("NODE_EDIT_DESCRIPTION"));

        super.onDraw();
    }

    protected T getBuilder() {
        return _builder;
    }
}
