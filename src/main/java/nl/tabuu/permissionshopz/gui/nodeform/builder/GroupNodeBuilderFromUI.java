package nl.tabuu.permissionshopz.gui.nodeform.builder;

import nl.tabuu.permissionshopz.data.node.GroupNode;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import nl.tabuu.tabuucore.inventory.ui.element.TextInput;
import nl.tabuu.tabuucore.inventory.ui.element.style.TextInputStyle;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.entity.Player;

public class GroupNodeBuilderFromUI extends NodeBuilderFormUI<GroupNode.Builder> {

    protected GroupNodeBuilderFromUI(GroupNode.Builder builder, InventoryUI returnUI) {
        super(builder, returnUI);
    }

    public GroupNodeBuilderFromUI(InventoryUI returnUI) {
        this(GroupNode.builder(), returnUI);
    }

    @Override
    protected void onDraw() {
        ItemBuilder
                paper = new ItemBuilder(XMaterial.PAPER)
                        .setDisplayName(_locale.translate("NODE_EDIT_GROUP_ID")),

                tag = new ItemBuilder(XMaterial.NAME_TAG);

        TextInputStyle groupIdInputStyle = new TextInputStyle(paper, tag, "Group ID");

        TextInput groupIdInput = new TextInput(groupIdInputStyle, this, this::onGroupIdInput);
        setElement(new Vector2f(1, 0), groupIdInput);

        super.onDraw();
    }

    private void onGroupIdInput(Player player, String permission) {
        getBuilder().setGroupId(permission);
    }
}