package nl.tabuu.permissionshopz.data.node;

import nl.tabuu.tabuucore.configuration.IDataHolder;

import java.util.Objects;

public class PermissionNode extends Node {
    private final String _permission;

    protected PermissionNode(NodeType type, String description, String permission) {
        super(type, description);
        _permission = permission;
    }

    protected PermissionNode(NodeType type, String description, IDataHolder data) {
        this(
                type,
                description,
                data.getString("Permission")
        );
    }

    public String getPermission() {
        return _permission;
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        data = super.serialize(data);
        data.set("Permission", getPermission());
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(String permission) {
        return builder().setPermission(permission);
    }

    public static class Builder extends Node.Builder {
        private String _permission = null;

        protected Builder(NodeType type) {
            super(type);
        }

        protected Builder() {
            this(NodeType.PERMISSION);
        }

        protected String getPermission() {
            return _permission;
        }

        public Builder setPermission(String permission) {
            _permission = permission;
            return this;
        }

        @Override
        public Builder setType(NodeType type) {
            super.setType(type);
            return this;
        }

        @Override
        public Builder setDescription(String description) {
            super.setDescription(description);
            return this;
        }

        @Override
        protected boolean canBuild() {
            return super.canBuild() && Objects.nonNull(getPermission());
        }

        @Override
        public PermissionNode build() {
            if(!canBuild()) throw new IllegalStateException("Cannot build Node.");
            return new PermissionNode(getType(), getDescription(), getPermission());
        }
    }
}