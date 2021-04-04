package nl.tabuu.permissionshopz.data.node;

import nl.tabuu.tabuucore.configuration.IDataHolder;

import java.util.Objects;

public class GroupNode extends Node {
    private final String _groupId;

    protected GroupNode(NodeType type, String description, String groupId) {
        super(type, description);
        _groupId = groupId;
    }

    protected GroupNode(NodeType type, String description, IDataHolder data) {
        this(
                type,
                description,
                data.getString("GroupID")
        );
    }

    public String getGroupId() {
        return _groupId;
    }

    @Override
    public Object[] getReplacements() {
        return new Object[] {
                "{TYPE}", getType(),
                "{DESCRIPTION}", getDescription(),
                "{GROUP_ID}", getGroupId()
        };
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        data = super.serialize(data);
        data.set("GroupID", getGroupId());
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(String group) {
        return builder().setGroupId(group);
    }

    public static class Builder extends Node.Builder {
        private String _groupId = null;

        protected Builder(NodeType type) {
            super(type);
        }

        protected Builder() {
            this(NodeType.GROUP);
        }

        protected String getGroupId() {
            return _groupId;
        }

        public Builder setGroupId(String groupId) {
            _groupId = groupId;
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
        public boolean canBuild() {
            return super.canBuild() && Objects.nonNull(getGroupId());
        }

        @Override
        public GroupNode build() {
            if(!canBuild()) throw new IllegalStateException("Cannot build Node.");
            return new GroupNode(getType(), getDescription(), getGroupId());
        }
    }
}