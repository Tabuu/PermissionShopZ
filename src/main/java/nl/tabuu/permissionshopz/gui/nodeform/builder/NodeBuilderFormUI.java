package nl.tabuu.permissionshopz.gui.nodeform.builder;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.gui.FutureSupplierInventoryFormUI;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import nl.tabuu.tabuucore.inventory.ui.element.TextInput;
import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.inventory.ui.element.style.TextInputStyle;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.util.Dictionary;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.entity.Player;

public abstract class NodeBuilderFormUI<T extends Node.Builder> extends FutureSupplierInventoryFormUI<Node> {

    private final T _builder;
    protected final InventoryUI _returnUI;

    protected final Dictionary _locale;
    protected final IConfiguration _config;

    public NodeBuilderFormUI(T builder, InventoryUI returnUI) {
        super("", InventorySize.ONE_BY_FIVE, returnUI);

        _builder = builder;
        _returnUI = returnUI;

        _locale = PermissionShopZ.getInstance().getLocale();
        _config = PermissionShopZ.getInstance().getConfiguration();
    }

    @Override
    protected void onDraw() {

        ItemBuilder
                paper = new ItemBuilder(XMaterial.PAPER)
                        .setDisplayName(_locale.translate("NODE_EDIT_DESCRIPTION")),

                tag = new ItemBuilder(XMaterial.NAME_TAG),

                emerald = new ItemBuilder(XMaterial.EMERALD)
                        .setDisplayName(_locale.translate("GUI_NAVIGATION_ACCEPT")),

                barrier = new ItemBuilder(XMaterial.BARRIER)
                        .setDisplayName(_locale.translate("GUI_NAVIGATION_INCOMPLETE_FORM"));

        TextInputStyle descriptionInputStyle = new TextInputStyle(paper, tag, "Description");
        Style acceptButtonStyle = new Style(emerald, barrier);

        TextInput descriptionInput = new TextInput(descriptionInputStyle, this, this::onDescriptionInput);
        Button acceptButton = new Button(acceptButtonStyle, this::onAcceptButton);

        acceptButton.setEnabled(getBuilder().canBuild());

        setElement(new Vector2f(0, 0), descriptionInput);
        setElement(new Vector2f(4, 0), acceptButton);

        super.onDraw();
    }

    private void onDescriptionInput(Player player, String description) {
        getBuilder().setDescription(description);
        onDraw();
    }

    private void onAcceptButton(Player player) {
        supply(player, getBuilder().build());
    }

    protected T getBuilder() {
        return _builder;
    }
}
