package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.string.AbstractStringSerializer;
import nl.tabuu.tabuucore.serialization.string.Serializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class NodeDAO implements DAO<Integer, Node> {

    private final TreeMap<Integer, Node> _nodes = new TreeMap<>();
    private final NodeSerializer _serializer = new NodeSerializer();

    @Nonnull
    @Override
    public Integer create(@Nonnull Node object) {
        int index = _nodes.lastKey() + 1;
        _nodes.put(index + 1, object);
        return index;
    }

    @Override
    public Node get(@Nonnull Integer id) {
        return _nodes.get(id);
    }

    @Nullable
    @Override
    public Integer getKey(@Nonnull Node value) {

        for(Map.Entry<Integer, Node> entry : _nodes.entrySet())
            if(Objects.equals(entry.getValue(), value)) return entry.getKey();

        return null;
    }

    @Nonnull
    @Override
    public Collection<Node> getAll() {
        return Collections.unmodifiableCollection(_nodes.values());
    }

    @Override
    public boolean update(@Nonnull Integer key, @Nonnull Node object) {
        _nodes.replace(key, object);
        return true;
    }

    @Override
    public boolean delete(@Nonnull Node object) {
        return _nodes.entrySet().removeIf(entry -> Objects.equals(entry.getValue(), object));
    }

    @Override
    public boolean delete(@Nonnull Integer key) {
        return Objects.nonNull(_nodes.remove(key));
    }

    @Override
    public boolean readAll() {
        if(!_nodes.isEmpty())
            _nodes.clear();

        IDataHolder data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("nodes.json");
        _nodes.putAll(data.getSerializableMap("Nodes", Node.class, Serializer.INTEGER));
        return true;
    }

    @Override
    public boolean writeAll() {
        IConfiguration data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("nodes.json");
        data.setSerializableMap("Nodes", _nodes, Serializer.INTEGER);
        data.save();
        return true;
    }

    public NodeSerializer getSerializer() {
        return _serializer;
    }

    public class NodeSerializer extends AbstractStringSerializer<Node> {
        @Override
        public Node deserialize(String key) {
            try {
                int index = Integer.parseInt(key);
                return get(index);
            } catch (NumberFormatException exception) {
                return null;
            }
        }

        @Override
        public String serialize(Node node) {
            Integer key = getKey(node);
            if(Objects.isNull(key)) return null;

            return String.valueOf(key);
        }
    }
}