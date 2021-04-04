package nl.tabuu.permissionshopz.gui.nodeform.builder;

import nl.tabuu.permissionshopz.data.node.PermissionNode;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import nl.tabuu.tabuucore.inventory.ui.element.TextInput;
import nl.tabuu.tabuucore.inventory.ui.element.style.TextInputStyle;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.entity.Player;

public class PermissionNodeBuilderFromUI extends NodeBuilderFormUI<PermissionNode.Builder> {

    protected PermissionNodeBuilderFromUI(PermissionNode.Builder builder, InventoryUI returnUI) {
        super(builder, returnUI);
    }

    public PermissionNodeBuilderFromUI(InventoryUI returnUI) {
        super(PermissionNode.builder(), returnUI);
    }

    @Override
    protected void onDraw() {
        ItemBuilder
                paper = new ItemBuilder(XMaterial.PAPER)
                        .setDisplayName(_locale.translate("NODE_EDIT_PERMISSION")),

                tag = new ItemBuilder(XMaterial.NAME_TAG);

        TextInputStyle permissionInputStyle = new TextInputStyle(paper, tag, "Permission");

        TextInput permissionInput = new TextInput(permissionInputStyle, this, this::onPermissionInput);
        setElement(new Vector2f(1, 0), permissionInput);

        super.onDraw();
    }

    private void onPermissionInput(Player player, String permission) {
        getBuilder().setPermission(permission);
        super.onDraw();
    }
}