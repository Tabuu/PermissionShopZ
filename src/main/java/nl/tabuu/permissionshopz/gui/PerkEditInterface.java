package nl.tabuu.permissionshopz.gui;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.PerkManager;
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
    private final PerkManager _manager;

    public PerkEditInterface(Perk perk) {
        super("Perk Editor", InventorySize.FIVE_ROWS);

        _local = PermissionShopZ.getInstance().getLocal();
        _perk = perk;
        _manager = PermissionShopZ.getInstance().getPerkManager();

        setTitle(_local.translate("GUI_PERK_EDITOR_TITLE"));
    }

    @Override
    protected void onDraw() {

        ItemBuilder
                paper = new ItemBuilder(XMaterial.PAPER)
                        .setDisplayName(_local.translate("GUI_PERK_EDITOR_NAME")),

                emerald = new ItemBuilder(XMaterial.EMERALD)
                        .setDisplayName(_local.translate("GUI_PERK_EDITOR_COST")),

                book = new ItemBuilder(XMaterial.BOOK)
                        .setDisplayName(_local.translate("GUI_PERK_EDITOR_PERMISSIONS"))
                        .setLore(_local.translate("GUI_PERK_EDITOR_PERMISSIONS_LORE")),

                book2 = new ItemBuilder(XMaterial.BOOK)
                        .setDisplayName(_local.translate("GUI_PERK_EDITOR_REQUIRED_PERMISSIONS"))
                        .setLore(_local.translate("GUI_PERK_EDITOR_REQUIRED_PERMISSIONS_LORE")),

                barrier = new ItemBuilder(XMaterial.BARRIER)
                        .setDisplayName(_local.translate("GUI_PERK_EDITOR_DELETE")),

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
        TextInputStyle entryStyle = new TextInputStyle(emerald.build(), XMaterial.NAME_TAG.parseItem(), "example.permission");
        ListEditorStyle awardedPermissionStyle = new ListEditorNodeStyle(
                book.build(),
                _local.translate("GUI_PERK_EDITOR_PERMISSIONS_ENTRY"),
                _local.translate("GUI_PERK_EDITOR_PERMISSIONS_ENTRY_CURRENT"),
                "{ENTRY}");
        ListEditorStyle requiredPermissionStyle = new ListEditorNodeStyle(
                book2.build(),
                _local.translate("GUI_PERK_EDITOR_REQUIRED_PERMISSIONS_ENTRY"),
                _local.translate("GUI_PERK_EDITOR_REQUIRED_PERMISSIONS_ENTRY_CURRENT"),
                "{ENTRY}");

        TextInput nameInput = new TextInput(nameStyle, this, this::onNameChange);
        TextInput costInput = new TextInput(costStyle, this, this::onCostChange);
        ItemInput itemInput = new ItemInput(itemStyle, true, this::onDisplayItemChange);
        Button deleteButton = new Button(deleteStyle, this::onDeleteClick);
        ListEditor<String> awardedPermissionInput = new ListEditor<>(
                awardedPermissionStyle,
                entryStyle,
                this,
                new ArrayList<>(_perk.getAwardedPermissions()),
                Serializer.STRING,
                this::onAwardedPermissionsChange);
        ListEditor<String> requiredPermissionInput = new ListEditor<>(
                requiredPermissionStyle,
                entryStyle,
                this,
                new ArrayList<>(_perk.getRequiredPermissions()),
                Serializer.STRING,
                this::onRequiredPermissionsChange);

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
        _manager.removePerk(_perk);
        close(player);
    }

    private void onNameChange(Player player, String name) {
        _perk.setName(name);
    }

    private void onAwardedPermissionsChange(Player player, List<String> list) {
        _perk.setAwardedPermissions(list);
        updateElement(new Vector2f(3, 2));
    }

    private void onRequiredPermissionsChange(Player player, List<String> list) {
        _perk.setRequiredPermissions(list);
        updateElement(new Vector2f(5, 2));
    }

    private void onCostChange(Player player, String value) {
        Double cost = Serializer.DOUBLE.deserialize(value);
        if(cost != null) _perk.setCost(cost);
    }
}