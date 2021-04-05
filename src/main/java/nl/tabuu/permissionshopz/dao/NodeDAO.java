package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.debug.Debug;
import nl.tabuu.tabuucore.serialization.string.Serializer;

import java.util.Collection;
import java.util.Objects;

public class NodeDAO extends IntegerTreeMapDAO<Node> {

    @Override
    public boolean readAll() {
        if(!_data.isEmpty())
            _data.clear();

        IDataHolder data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("nodes.json");
        _data.putAll(data.getSerializableMap("Nodes", Node.class, Serializer.INTEGER));
        return true;
    }

    @Override
    public boolean writeAll() {
        IConfiguration data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("nodes.json");
        data.setSerializableMap("Nodes", _data, Serializer.INTEGER);
        data.save();
        return true;
    }

    @Override
    public boolean isGarbage(Node node) {
        if(Objects.isNull(node)) return true;

        Collection<Perk> perks = PermissionShopZ.getInstance().getPerkDao().getAll();
        return perks.stream().noneMatch(perk -> perk.getAwardedNodes().contains(node) || perk.getRequiredNodes().contains(node));
    }
}