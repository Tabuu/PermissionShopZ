package nl.tabuu.permissionshopz.gui.nodeform.builder;

import nl.tabuu.permissionshopz.data.node.TemporaryPermissionNode;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import nl.tabuu.tabuucore.inventory.ui.element.TextInput;
import nl.tabuu.tabuucore.inventory.ui.element.style.TextInputStyle;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TemporaryPermissionNodeBuilderFromUI extends PermissionNodeBuilderFromUI {

    protected TemporaryPermissionNodeBuilderFromUI(TemporaryPermissionNode.Builder builder, InventoryUI returnUI) {
        super(builder, returnUI);
    }

    public TemporaryPermissionNodeBuilderFromUI(InventoryUI returnUI) {
        super(TemporaryPermissionNode.builder(), returnUI);
    }

    @Override
    protected void onDraw() {
        ItemBuilder
                clock = new ItemBuilder(XMaterial.CLOCK)
                        .setDisplayName(_locale.translate("NODE_EDIT_DURATION")),

                tag = new ItemBuilder(XMaterial.NAME_TAG);

        TextInputStyle durationInputStyle = new TextInputStyle(clock, tag, "Duration");

        TextInput durationInput = new TextInput(durationInputStyle, this, this::onDurationInput);
        setElement(new Vector2f(2, 0), durationInput);

        super.onDraw();
    }

    @Override
    protected TemporaryPermissionNode.Builder getBuilder() {
        return (TemporaryPermissionNode.Builder) super.getBuilder();
    }

    private void onDurationInput(Player player, String durationString) {
        Long duration = Serializer.TIME.deserialize(durationString);
        if(Objects.isNull(duration)) return;

        getBuilder().setDuration(duration);
        super.onDraw();
    }
}