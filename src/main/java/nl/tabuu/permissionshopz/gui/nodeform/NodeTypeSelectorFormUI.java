package nl.tabuu.permissionshopz.gui.nodeform;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.data.node.NodeType;
import nl.tabuu.permissionshopz.gui.FutureSupplierInventoryFormUI;
import nl.tabuu.permissionshopz.gui.nodeform.builder.*;
import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.util.Dictionary;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NodeTypeSelectorFormUI extends FutureSupplierInventoryFormUI<Node> {

    private final InventoryUI _returnUI;

    private final Dictionary _locale;

    public NodeTypeSelectorFormUI(InventoryUI returnUI) {
        super("", InventorySize.ONE_BY_FIVE, returnUI);
        _returnUI = returnUI;

        _locale = PermissionShopZ.getInstance().getLocale();
    }

    @Override
    protected void onDraw() {

        ItemBuilder
                barrier = new ItemBuilder(XMaterial.BARRIER)
                        .setDisplayName(_locale.translate("GUI_NAVIGATION_RETURN"));

        Style returnButtonStyle = new Style(barrier);

        Button returnButton = new Button(returnButtonStyle, _returnUI::open);

        setElement(new Vector2f(4, 0), returnButton);

        NodeType[] nodeTypes = NodeType.values();
        for (int i = 0; i < 4; i++) {
            NodeType nodeType = nodeTypes[i];
            Vector2f position = new Vector2f(i, 0);

            Style buttonStyle = new Style(nodeType.getIcon());
            Button button = new Button(buttonStyle, player -> onNodeTypeClick(player, nodeType));

            setElement(position, button);
        }

        super.onDraw();
    }

    private void onNodeTypeClick(Player player, NodeType nodeType) {
        NodeBuilderFormUI<?> nodeBuilderForm;

        switch (nodeType) {

            case PERMISSION:
                nodeBuilderForm = new PermissionNodeBuilderFromUI(_returnUI);
                break;
            case TEMPORARY_PERMISSION:
                nodeBuilderForm = new TemporaryPermissionNodeBuilderFromUI(_returnUI);
                break;
            case GROUP:
                nodeBuilderForm = new GroupNodeBuilderFromUI(_returnUI);
                break;
            case TRACK:
                nodeBuilderForm = new TrackNodeBuilderFromUI(_returnUI);
                break;

            default:
                return;
        }

        nodeBuilderForm.onSupply(this::supply);
        Bukkit.getScheduler().runTask(PermissionShopZ.getInstance(), () -> nodeBuilderForm.open(player));
    }
}