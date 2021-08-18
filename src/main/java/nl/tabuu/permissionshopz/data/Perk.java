package nl.tabuu.permissionshopz.data;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.dao.NodeDAO;
import nl.tabuu.permissionshopz.data.node.Node;
import nl.tabuu.permissionshopz.nodehandler.INodeHandler;
import nl.tabuu.permissionshopz.util.NumberFormat;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.configuration.holder.JsonDataHolder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.serialization.ISerializable;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collector;

public class Perk implements ISerializable<IDataHolder> {
    @Nonnull
    private String _name;
    @Nonnull
    private ItemStack _displayItem;
    @Nonnull
    private final Set<Integer> _awardedPermissions, _requiredPermissions;
    private double _cost;

    public Perk(@Nonnull String name, double cost, @Nonnull ItemStack displayItem, @Nonnull List<Integer> awardedPermissions, @Nonnull List<Integer> requiredPermissions) {
        _name = name;
        _cost = cost;
        _displayItem = displayItem;
        _awardedPermissions = new LinkedHashSet<>(awardedPermissions);
        _requiredPermissions = new LinkedHashSet<>(requiredPermissions);
    }

    public Perk(IDataHolder data) {
        this(
                data.getString("Name", "Undefined"),
                data.getDouble("Cost", 0.0d),
                data.get("Item", Serializer.ITEMSTACK, XMaterial.BARRIER.parseItem()),
                data.getList("AwardedNodes", getNodeDao().getKeySerializer()),
                data.getList("RequiredNodes", getNodeDao().getKeySerializer())
        );
    }

    public Perk() {
        this(new JsonDataHolder());
    }

    @Nonnull
    public String getName() {
        return _name;
    }

    public double getCost() {
        return _cost;
    }

    @Nonnull public ItemStack getDisplayItem() {
        if (_displayItem.getType().equals(Material.AIR))
            setDisplayItem(XMaterial.BARRIER.parseItem());

        return _displayItem.clone();
    }

    public void apply(Player player) {
        INodeHandler handler = PermissionShopZ.getInstance().getNodeHandler();
        for (Node node : getAwardedNodes()) handler.addNode(player, node);
    }

    public Object[] getReplacements() {
        return new Object[]{
                "{COST}", NumberFormat.formatNumber(getCost()),
                "{NAME}", getName()
        };
    }

    public void setName(@Nonnull String name) {
        _name = name;
    }

    public void setCost(double cost) {
        _cost = cost;
    }

    public void setDisplayItem(@Nonnull ItemStack displayItem) {
        _displayItem = displayItem;
    }

    @Nonnull
    public List<Node> getAwardedNodes() {
        return _awardedPermissions.stream()
                .map(getNodeDao().getSerializer()::serialize)
                .filter(Objects::nonNull)
                .collect(Collector.of(ArrayList<Node>::new, List::add, (left, right) -> {
                    left.addAll(right);
                    return left;
                }, Collections::unmodifiableList));
    }

    public boolean addAwardedNode(Node node) {
        Integer integer = PermissionShopZ.getInstance().getNodeDao().getKey(node);
        if(Objects.isNull(integer)) return false;

        return _awardedPermissions.add(integer);
    }

    public void setAwardedNodes(@Nonnull List<Node> awardedNodes) {
        _awardedPermissions.clear();
        awardedNodes.forEach(this::addAwardedNode);
    }

    @Nonnull
    public List<Node> getRequiredNodes() {
        return _requiredPermissions.stream()
                .map(getNodeDao().getSerializer()::serialize)
                .filter(Objects::nonNull)
                .collect(Collector.of(ArrayList<Node>::new, List::add, (left, right) -> {
                    left.addAll(right);
                    return left;
                }, Collections::unmodifiableList));
    }

    public boolean addRequiredNode(Node node) {
        Integer integer = PermissionShopZ.getInstance().getNodeDao().getKey(node);
        if(Objects.isNull(integer)) return false;

        return _requiredPermissions.add(integer);
    }

    public void setRequiredNodes(@Nonnull List<Node> requiredPermissions) {
        _requiredPermissions.clear();
        requiredPermissions.forEach(this::addRequiredNode);
    }

    public boolean hasRequiredNodes(Player player) {
        INodeHandler handler = PermissionShopZ.getInstance().getNodeHandler();

        for(Node node : getRequiredNodes()) {
            if(!handler.hasNode(player, node))
                return false;
        }

        return true;
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        _awardedPermissions.removeIf(integer -> Objects.isNull(integer) || Objects.isNull(getNodeDao().get(integer)));
        _requiredPermissions.removeIf(integer -> Objects.isNull(integer) || Objects.isNull(getNodeDao().get(integer)));

        data.set("Name", getName());
        data.set("Cost", getCost());
        data.set("Item", getDisplayItem(), Serializer.ITEMSTACK);
        data.setList("AwardedNodes", new LinkedList<>(_awardedPermissions), getNodeDao().getKeySerializer());
        data.setList("RequiredNodes", new LinkedList<>(_requiredPermissions), getNodeDao().getKeySerializer());

        return data;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Perk)) return false;
        Perk perk = (Perk) object;
        return Double.compare(perk.getCost(), getCost()) == 0 &&
                getName().equals(perk.getName()) &&
                Objects.equals(getDisplayItem(), perk.getDisplayItem()) &&
                getAwardedNodes().equals(perk.getAwardedNodes()) &&
                getRequiredNodes().equals(perk.getRequiredNodes());
    }

    private static NodeDAO getNodeDao() {
        return PermissionShopZ.getInstance().getNodeDao();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCost(), getDisplayItem(), getAwardedNodes(), getRequiredNodes());
    }
}