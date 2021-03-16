package nl.tabuu.permissionshopz.data;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.permissionhandler.IPermissionHandler;
import nl.tabuu.permissionshopz.util.NumberFormat;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.serialization.ISerializable;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Perk implements ISerializable<IDataHolder> {
    @Nonnull
    private String _name;
    private double _cost;
    private ItemStack _displayItem;
    @Nonnull
    private List<String> _awardedPermissions, _requiredPermissions;

    public Perk(@Nonnull String name, double cost, @Nonnull ItemStack displayItem, @Nonnull List<String> awardedPermissions, @Nonnull List<String> requiredPermissions) {
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
                data.getStringList("Permissions"),
                data.getStringList("RequiredPermissions")
        );
    }

    @Nonnull
    public String getName() {
        return _name;
    }

    public double getCost() {
        return _cost;
    }

    @Nonnull public ItemStack getDisplayItem() {
        if (_displayItem == null || _displayItem.getType().equals(Material.AIR))
            setDisplayItem(XMaterial.BARRIER.parseItem());

        return _displayItem.clone();
    }

    @Nonnull
    public List<String> getAwardedPermissions() {
        return _awardedPermissions;
    }

    public void apply(Player player) {
        IPermissionHandler handler = PermissionShopZ.getInstance().getPermissionHandler();
        for (String node : getAwardedPermissions()) handler.addNode(player, node);
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

    public void setDisplayItem(ItemStack displayItem) {
        _displayItem = displayItem;
    }

    public void setAwardedPermissions(@Nonnull List<String> permissions) {
        _awardedPermissions = permissions;
    }

    @Nonnull
    public List<String> getRequiredPermissions() {
        return _requiredPermissions;
    }

    public void setRequiredPermissions(@Nonnull List<String> requiredPermissions) {
        _requiredPermissions = requiredPermissions;
    }

    public boolean hasRequiredPermissions(Player player) {
        IPermissionHandler permissionHandler = PermissionShopZ.getInstance().getPermissionHandler();

        for(String permission : getRequiredPermissions()) {
            if(!permissionHandler.hasNode(player, permission))
                return false;
        }

        return true;
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        data.set("Name", _name);
        data.set("Cost", _cost);
        data.set("Item", _displayItem, Serializer.ITEMSTACK);
        data.setStringList("Permissions", _awardedPermissions);
        data.setStringList("RequiredPermissions", _requiredPermissions);

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

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCost(), getDisplayItem(), getAwardedPermissions());
    }
}