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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Perk implements ISerializable<IDataHolder> {
    @Nonnull
    private String _name;
    @Nonnull
    private ItemStack _displayItem;
    @Nonnull
    private final List<Node> _awardedPermissions, _requiredPermissions;
    private double _cost;

    public Perk(@Nonnull String name, double cost, @Nonnull ItemStack displayItem, @Nonnull List<Node> awardedPermissions, @Nonnull List<Node> requiredPermissions) {
        _name = name;
        _cost = cost;
        _displayItem = displayItem;
        _awardedPermissions = new LinkedList<>(awardedPermissions);
        _requiredPermissions = new LinkedList<>(requiredPermissions);
    }

    public Perk(IDataHolder data) {
        this(
                data.getString("Name", "Undefined"),
                data.getDouble("Cost", 0.0d),
                data.get("Item", Serializer.ITEMSTACK, XMaterial.BARRIER.parseItem()),
                data.getList("AwardedNodes", getNodeSerializer()),
                data.getList("RequiredPermissions", getNodeSerializer())
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
        INodeHandler handler = PermissionShopZ.getInstance().getPermissionHandler();
        for (Node node : getAwardedPermissions()) handler.addNode(player, node);
    }

    public Object[] getReplacements() {
        return new Object[]{
                "{PRICE}", NumberFormat.formatNumber(getCost()),
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
    public List<Node> getAwardedPermissions() {
        return Collections.unmodifiableList(_awardedPermissions);
    }

    public void setAwardedPermissions(@Nonnull List<Node> nodes) {
        _awardedPermissions.clear();
        _awardedPermissions.addAll(nodes);
    }

    @Nonnull
    public List<Node> getRequiredPermissions() {
        return Collections.unmodifiableList(_requiredPermissions);
    }

    public void setRequiredPermissions(@Nonnull List<Node> requiredPermissions) {
        _requiredPermissions.clear();
        _requiredPermissions.addAll(requiredPermissions);
    }

    public boolean hasRequiredPermissions(Player player) {
        INodeHandler handler = PermissionShopZ.getInstance().getPermissionHandler();

        for(Node node : getRequiredPermissions()) {
            if(!handler.hasNode(player, node))
                return false;
        }

        return true;
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        data.set("Name", _name);
        data.set("Cost", _cost);
        data.set("Item", _displayItem, Serializer.ITEMSTACK);
        data.setList("Permissions", _awardedPermissions, getNodeSerializer());
        data.setList("RequiredPermissions", _requiredPermissions, getNodeSerializer());

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
                getAwardedPermissions().equals(perk.getAwardedPermissions());
    }

    private static NodeDAO.NodeSerializer getNodeSerializer() {
        return PermissionShopZ.getInstance().getNodeDao().getSerializer();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCost(), getDisplayItem(), getAwardedPermissions());
    }
}