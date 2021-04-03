package nl.tabuu.permissionshopz.data.node;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.string.Serializer;

public class TemporaryPermissionNode extends PermissionNode {
    private final long _duration;

    protected TemporaryPermissionNode(NodeType type, String description, String permission, long duration) {
        super(type, description, permission);
        _duration = duration;
    }

    protected TemporaryPermissionNode(NodeType type, String description, IDataHolder data) {
        this(
                type,
                description,
                data.getString("Permission"),
                data.get("Duration", Serializer.TIME, 0L)
        );
    }

    public long getDuration() {
        return _duration;
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        data = super.serialize(data);
        data.set("Duration", getDuration(), Serializer.TIME);
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(String permission) {
        return new Builder().setPermission(permission);
    }

    public static class Builder extends PermissionNode.Builder {

        private long _duration = 0;

        protected Builder(NodeType type) {
            super(type);
        }

        protected Builder() {
            this(NodeType.TEMPORARY_PERMISSION);
        }

        protected long getDuration() {
            return _duration;
        }

        public Builder setDuration(long duration) {
            _duration = duration;
            return this;
        }

        @Override
        public PermissionNode.Builder setType(NodeType type) {
            super.setType(type);
            return this;
        }

        @Override
        public PermissionNode.Builder setDescription(String description) {
            super.setDescription(description);
            return this;
        }

        @Override
        public Builder setPermission(String permission) {
            super.setPermission(permission);
            return this;
        }

        @Override
        public TemporaryPermissionNode build() {
            if(!canBuild()) throw new IllegalStateException("Cannot build Node.");
            return new TemporaryPermissionNode(getType(), getDescription(), getPermission(), getDuration());
        }
    }
}