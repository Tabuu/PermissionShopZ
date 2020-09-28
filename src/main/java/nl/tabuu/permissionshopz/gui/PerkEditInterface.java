package nl.tabuu.permissionshopz.gui;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.PerkManager;
import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryFormUI;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
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

    private Perk _perk;
    private Dictionary _local;
    private PerkManager _manager;

    public PerkEditInterface(Perk perk) {
        super("Perk Editor", InventorySize.FOUR_ROWS);

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
        ListEditorStyle permissionStyle = new ListEditorStyle(
                book.build(),
                _local.translate("GUI_PERK_EDITOR_PERMISSIONS_ENTRY"),
                _local.translate("GUI_PERK_EDITOR_PERMISSIONS_ENTRY_CURRENT"),
                "{ENTRY}");

        TextInput nameInput = new TextInput(nameStyle, this, this::onNameChange);
        TextInput costInput = new TextInput(costStyle, this, this::onCostChange);
        ItemInput itemInput = new ItemInput(itemStyle, true, this::onDisplayItemChange);
        Button deleteButton = new Button(deleteStyle, this::onDeleteClick);
        ListEditor<String> permissionInput = new ListEditor<>(
                permissionStyle,
                entryStyle,
                this,
                new ArrayList<>(_perk.getPermissions()),
                Serializer.STRING,
                this::onPermissionsChange);

        setElement(new Vector2f(1, 1), nameInput);
        setElement(new Vector2f(3, 1), costInput);
        setElement(new Vector2f(4, 2), deleteButton);
        setElement(new Vector2f(5, 1), itemInput);
        setElement(new Vector2f(7, 1), permissionInput);

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

    private void onPermissionsChange(Player player, List<String> list) {
        _perk.setPermissions(list);
        updateElement(new Vector2f(7, 1));
    }

    private void onCostChange(Player player, String value) {
        Double cost = Serializer.DOUBLE.deserialize(value);
        if(cost != null) _perk.setCost(cost);
    }

}
