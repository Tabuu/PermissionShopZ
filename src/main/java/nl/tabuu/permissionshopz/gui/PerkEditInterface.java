package nl.tabuu.permissionshopz.gui;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.gui.element.ListEditor;
import nl.tabuu.permissionshopz.gui.element.ListEditorStyle;
import nl.tabuu.permissionshopz.gui.nodeform.NodeTypeSelectorFormUI;
import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryFormUI;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import nl.tabuu.tabuucore.inventory.ui.element.ItemInput;
import nl.tabuu.tabuucore.inventory.ui.element.TextInput;
import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.inventory.ui.element.style.TextInputStyle;
import nl.tabuu.tabuucore.inventory.ui.graphics.brush.CheckerBrush;
import nl.tabuu.tabuucore.inventory.ui.graphics.brush.IBrush;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import nl.tabuu.tabuucore.util.Dictionary;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerkEditInterface extends InventoryFormUI {

    private final Perk _perk;
    private final Dictionary _local;

    public PerkEditInterface(Perk perk) {
        super("Perk Editor", InventorySize.FIVE_ROWS);

        _local = PermissionShopZ.getInstance().getLocale();
        _perk = perk;

        setTitle(_local.translate("GUI_PERK_EDITOR_TITLE"));
    }

    @Override
    protected void onDraw() {
        ItemBuilder
                paper = new ItemBuilder(XMaterial.PAPER)
                .setDisplayName(_local.translate("PERK_EDIT_NAME")),

                emerald = new ItemBuilder(XMaterial.EMERALD)
                        .setDisplayName(_local.translate("PERK_EDIT_COST")),

                book = new ItemBuilder(XMaterial.BOOK)
                        .setDisplayName(_local.translate("PERK_EDIT_NODES_AWARDED"))
                        .setLore(_local.translate("GUI_ELEMENT_LIST_EDITOR_LORE")),

                book2 = new ItemBuilder(XMaterial.BOOK)
                        .setDisplayName(_local.translate("PERK_EDIT_NODES_REQUIRED"))
                        .setLore(_local.translate("GUI_ELEMENT_LIST_EDITOR_LORE")),

                barrier = new ItemBuilder(XMaterial.BARRIER)
                        .setDisplayName(_local.translate("PERK_EDIT_DELETE")),

                black = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE)
                        .setDisplayName(" "),

                gray = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                        .setDisplayName(" ");

        IBrush brush = new CheckerBrush(black.build(), gray.build());
        setBrush(brush);

        Vector2f borderStart = new Vector2f(0, 0);
        Vector2f borderStop = new Vector2f(getSize().getWidth() - 1, getSize().getHeight() - 1);
        drawFilledRectangle(borderStart, borderStop);

        Style deleteStyle = new Style(barrier.build());
        Style itemStyle = new Style(_perk.getDisplayItem().clone());
        TextInputStyle nameStyle = new TextInputStyle(paper.build(), XMaterial.NAME_TAG.parseItem(), _perk.getName());
        TextInputStyle costStyle = new TextInputStyle(emerald.build(), XMaterial.NAME_TAG.parseItem(), Double.toString(_perk.getCost()));
        ListEditorStyle awardedPermissionStyle = new ListEditorStyle(
                book.build(),
                _local.translate("GUI_ELEMENT_LIST_EDITOR_ENTRY"),
                _local.translate("GUI_ELEMENT_LIST_EDITOR_ENTRY_SELECTED"),
                "{ENTRY}");
        ListEditorStyle requiredPermissionStyle = new ListEditorStyle(
                book2.build(),
                _local.translate("GUI_ELEMENT_LIST_EDITOR_ENTRY"),
                _local.translate("GUI_ELEMENT_LIST_EDITOR_ENTRY_SELECTED"),
                "{ENTRY}");

        TextInput nameInput = new TextInput(nameStyle, this, this::onNameChange);
        TextInput costInput = new TextInput(costStyle, this, this::onCostChange);
        ItemInput itemInput = new ItemInput(itemStyle, true, this::onDisplayItemChange);
        Button deleteButton = new Button(deleteStyle, this::onDeleteClick);

        NodeTypeSelectorFormUI awardedNodeSupplier = new NodeTypeSelectorFormUI(this);
        NodeTypeSelectorFormUI requiredNodeSupplier = new NodeTypeSelectorFormUI(this);

        ListEditor<Node> awardedPermissionInput = new ListEditor<>(
                awardedPermissionStyle,
                new ArrayList<>(_perk.getAwardedNodes()),
                awardedNodeSupplier)
                .onChange(this::onAwardedPermissionsChange)
                .onAdd((player, node) -> PermissionShopZ.getInstance().getNodeDao().create(node));

        ListEditor<Node> requiredPermissionInput = new ListEditor<>(
                requiredPermissionStyle,
                new ArrayList<>(_perk.getRequiredNodes()),
                requiredNodeSupplier)
                .onChange(this::onRequiredPermissionsChange)
                .onAdd((player, node) -> PermissionShopZ.getInstance().getNodeDao().create(node));

        setElement(new Vector2f(2, 1), nameInput);
        setElement(new Vector2f(4, 1), costInput);
        setElement(new Vector2f(6, 1), itemInput);
        setElement(new Vector2f(3, 2), awardedPermissionInput);
        setElement(new Vector2f(5, 2), requiredPermissionInput);
        setElement(new Vector2f(4, 3), deleteButton);

        itemInput.setValue(_perk.getDisplayItem());

        super.onDraw();
    }

    public void onDisplayItemChange(Player player, ItemStack item) {
        _perk.setDisplayItem(item);
    }

    public void onDeleteClick(Player player) {
        PermissionShopZ.getInstance().getPerkDao().delete(_perk);
        close(player);
    }

    private void onNameChange(Player player, String name) {
        _perk.setName(name);
    }

    private void onAwardedPermissionsChange(Player player, List<Node> list) {
        _perk.setAwardedNodes(list);
        updateElement(new Vector2f(3, 2));
    }

    private void onRequiredPermissionsChange(Player player, List<Node> list) {
        _perk.setRequiredPermissions(list);
        updateElement(new Vector2f(5, 2));
    }

    private void onCostChange(Player player, String value) {
        Double cost = Serializer.DOUBLE.deserialize(value);
        if (cost != null) _perk.setCost(cost);
    }
}