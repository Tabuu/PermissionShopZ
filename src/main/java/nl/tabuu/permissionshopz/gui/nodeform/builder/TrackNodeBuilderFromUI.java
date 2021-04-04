package nl.tabuu.permissionshopz.gui.nodeform.builder;

import nl.tabuu.permissionshopz.data.node.TrackNode;
import nl.tabuu.tabuucore.inventory.ui.InventoryUI;
import nl.tabuu.tabuucore.inventory.ui.element.TextInput;
import nl.tabuu.tabuucore.inventory.ui.element.style.TextInputStyle;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TrackNodeBuilderFromUI extends NodeBuilderFormUI<TrackNode.Builder> {

    protected TrackNodeBuilderFromUI(TrackNode.Builder builder, InventoryUI returnUI) {
        super(builder, returnUI);
    }

    public TrackNodeBuilderFromUI(InventoryUI returnUI) {
        this(TrackNode.builder(), returnUI);
    }

    @Override
    protected void onDraw() {
        ItemBuilder
                rail = new ItemBuilder(XMaterial.POWERED_RAIL)
                        .setDisplayName(_locale.translate("NODE_EDIT_TRACK_ID")),

                expBottle = new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE)
                        .setDisplayName(_locale.translate("NODE_EDIT_TRACK_INDEX")),

                tag = new ItemBuilder(XMaterial.NAME_TAG);

        TextInputStyle
                trackIdInputStyle = new TextInputStyle(rail, tag, "Track ID"),
                trackIndexInputStyle = new TextInputStyle(rail, tag, "Track Level");

        TextInput
                trackIdInput = new TextInput(trackIdInputStyle, this, this::onTrackIdInput),
                trackIndexInput = new TextInput(trackIndexInputStyle, this, this::onTrackIndexInput);

        setElement(new Vector2f(1, 0), trackIdInput);
        setElement(new Vector2f(2, 0), trackIndexInput);

        super.onDraw();
    }

    private void onTrackIdInput(Player player, String trackId) {
        getBuilder().setTrackId(trackId);
        super.onDraw();
    }

    private void onTrackIndexInput(Player player, String trackIndexString) {
        Integer index = Serializer.INTEGER.deserialize(trackIndexString);
        if(Objects.isNull(index)) return;

        getBuilder().setIndex(index);
        super.onDraw();
    }
}