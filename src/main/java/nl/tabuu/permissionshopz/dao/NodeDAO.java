package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.serialization.string.AbstractStringSerializer;
import nl.tabuu.tabuucore.serialization.string.Serializer;

import java.util.Collection;
import java.util.Objects;

public class NodeDAO extends IntegerMapDAO<Node> implements IConfigurationDAO<Integer, Node> {

    @Override
    public String getConfigurationKey() {
        return "Nodes";
    }

    @Override
    public IConfiguration getConfiguration() {
        return PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("nodes.json");
    }

    @Override
    public AbstractStringSerializer<Integer> getKeySerializer() {
        return Serializer.INTEGER;
    }

    @Override
    public boolean isGarbage(Node node) {
        if(Objects.isNull(node)) return true;

        Collection<Perk> perks = PermissionShopZ.getInstance().getPerkDao().getAll();
        return perks.stream().noneMatch(perk -> perk.getAwardedNodes().contains(node) || perk.getRequiredNodes().contains(node));
    }
}