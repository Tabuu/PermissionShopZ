package nl.tabuu.permissionshopz.gui.nodeform;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.data.node.NodeType;
import nl.tabuu.permissionshopz.gui.FutureSupplierInventoryFormUI;
import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.util.Dictionary;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.entity.Player;

public class NodeTypeSelectorFormUI extends FutureSupplierInventoryFormUI<Node> {

    private Dictionary _locale;

    public NodeTypeSelectorFormUI(InventoryUI returnUI) {
        super("", InventorySize.ONE_BY_FIVE, returnUI);
        _locale = PermissionShopZ.getInstance().getLocale();
    }

    @Override
    protected void onDraw() {

        NodeType[] nodeTypes = NodeType.values();
        for (int i = 0; i < 5; i++) {
            NodeType nodeType = nodeTypes[i];
            Vector2f position = new Vector2f(i, 0);
            setElement(position, getButton(nodeType));
        }

        super.onDraw();
    }

    protected Button getButton(NodeType nodeType) {
        ItemBuilder item;

        switch (nodeType) {
            case PERMISSION:
                item = new ItemBuilder(XMaterial.PAPER);
                break;
            case TEMPORARY_PERMISSION:
                item = new ItemBuilder(XMaterial.CLOCK);
                break;
            case GROUP:
                item = new ItemBuilder(XMaterial.BOOK);
                break;
            case TRACK:
                item = new ItemBuilder(XMaterial.POWERED_RAIL);
                break;
            default:
                item = new ItemBuilder(XMaterial.BARRIER);
                break;
        }

        item.setDisplayName(nodeType.toString());
        Style style = new Style(item);
        return new Button(style, (player) -> onNodeTypeClick(player, nodeType));
    }

    private void onNodeTypeClick(Player player, NodeType nodeType) {

    }
}