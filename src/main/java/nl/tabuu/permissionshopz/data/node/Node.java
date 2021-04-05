package nl.tabuu.permissionshopz.data.node;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.nodehandler.INodeHandler;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;
import nl.tabuu.tabuucore.util.Dictionary;
import org.bukkit.entity.Player;

import java.util.Objects;

public abstract class Node implements ISerializable<IDataHolder> {
    private final NodeType _type;
    private final String _description;

    protected Node(NodeType type, String description) {
        _type = type;
        _description = description;
    }

    public boolean apply(Player player) {
        return getNodeHandler().addNode(player, this);
    }

    public NodeType getType() {
        return _type;
    }

    public String getDescription() {
        return _description;
    }

    protected INodeHandler getNodeHandler() {
        return PermissionShopZ.getInstance().getPermissionHandler();
    }

    public Object[] getReplacements() {
        return new Object[] {
                "{TYPE}", getType(),
                "{DESCRIPTION}", getDescription()
        };
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        data.set("Type", getType(), NodeType::name);
        data.set("Description", getDescription());
        return data;
    }

    @Override
    public String toString() {
        Dictionary locale = PermissionShopZ.getInstance().getLocale();
        return locale.translate("NODE_TO_STRING_" + getType().name(), getReplacements());
    }

    private static Node deserialize(IDataHolder data) {
        NodeType type = data.get("Type", NodeType::valueOf, NodeType.UNKNOWN);
        String description = data.getString("Description");

        switch (type) {
            case PERMISSION:
                return new PermissionNode(type, description, data);

            case TEMPORARY_PERMISSION:
                return new TemporaryPermissionNode(type, description, data);

            case GROUP:
                return new GroupNode(type, description, data);

            case TRACK:
                return new TrackNode(type, description, data);

            default:
                return null;
        }
    }

    public static abstract class Builder {
        private NodeType _type;
        private String _description = "";

        protected Builder(NodeType type) {
            _type = type;
        }

        protected NodeType getType() {
            return _type;
        }

        public Builder setType(NodeType type) {
            _type = type;
            return this;
        }

        protected String getDescription() {
            return _description;
        }

        public Builder setDescription(String description) {
            _description = description;
            return this;
        }

        public boolean canBuild() {
            return Objects.nonNull(getType()) && Objects.nonNull(getDescription());
        }

        public abstract Node build();
    }
}