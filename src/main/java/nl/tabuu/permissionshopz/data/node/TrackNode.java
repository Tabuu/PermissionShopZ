package nl.tabuu.permissionshopz.data.node;

import nl.tabuu.tabuucore.configuration.IDataHolder;

import java.util.Objects;

public class TrackNode extends Node {
    private final String _trackId;
    private final int _index;

    protected TrackNode(NodeType type, String description, String trackId, int index) {
        super(type, description);
        _trackId = trackId;
        _index = index;
    }

    protected TrackNode(NodeType type, String description, IDataHolder data) {
        this(
                type,
                description,
                data.getString("TrackID"),
                data.getInteger("Level", 0)
        );
    }

    public String getTrackId() {
        return _trackId;
    }

    public int getIndex() {
        return _index;
    }

    @Override
    public Object[] getReplacements() {
        return new Object[] {
                "{TYPE}", getType(),
                "{DESCRIPTION}", getDescription(),
                "{TRACK_ID}", getTrackId(),
                "{TRACK_INDEX}", getIndex()
        };
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        data = super.serialize(data);
        data.set("TrackID", getTrackId());
        data.set("Level", getIndex());
        return data;
    }
    
        public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(String group) {
        return builder().setTrackId(group);
    }

    public static class Builder extends Node.Builder {
        private String _trackId = null;
        private int _index = 0;

        protected Builder(NodeType type) {
            super(type);
        }

        private Builder() {
            this(NodeType.TRACK);
        }

        protected String getTrackId() {
            return _trackId;
        }

        public Builder setTrackId(String trackId) {
            _trackId = trackId;
            return this;
        }

        protected int getIndex() {
            return _index;
        }

        public Builder setIndex(int index) {
            _index = index;
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
            return super.canBuild() && Objects.nonNull(getTrackId());
        }

        @Override
        public TrackNode build() {
            if(!canBuild()) throw new IllegalStateException("Cannot build Node.");
            return new TrackNode(getType(), getDescription(), getTrackId(), getIndex());
        }
    }
}