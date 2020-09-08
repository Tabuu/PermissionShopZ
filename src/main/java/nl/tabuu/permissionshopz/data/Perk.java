package nl.tabuu.permissionshopz.data;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.permissionhandler.IPermissionHandler;
import nl.tabuu.permissionshopz.util.NumberFormat;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.material.XMaterial;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class Perk {
    private UUID _uuid;
    private String _name;
    private double _cost;
    private ItemStack _displayItem;
    private List<String> _permissions;

    public Perk(String name, double cost, ItemStack displayItem, List<String> permissions){
        _name = name;
        _cost = cost;
        _displayItem = displayItem;
        _permissions = permissions;

        do {
            _uuid = UUID.randomUUID();
        } while (PermissionShopZ.getInstance().getPerkManager().getPerk(_uuid) != null);
    }

    public Perk(IDataHolder data) {
        _uuid = data.get("UUID", Serializer.UUID);
        _name = data.getString("Name");
        _cost = data.getDouble("Cost");
        _displayItem = data.get("Item", Serializer.ITEMSTACK);
        _permissions = data.getStringList("Permissions");
    }

    public UUID getUniqueId(){
        return _uuid;
    }

    public String getName(){
        return _name;
    }

    public double getCost(){
        return _cost;
    }

    public ItemStack getDisplayItem() {
        if (_displayItem == null || _displayItem.getType().equals(Material.AIR))
            setDisplayItem(XMaterial.BARRIER.parseItem());

        return _displayItem.clone();
    }

    public List<String> getPermissions(){
        return _permissions;
    }

    public void apply(Player player) {
        IPermissionHandler handler = PermissionShopZ.getInstance().getPermissionHandler();
        for(String node : _permissions) handler.addPermission(player, node);
    }

    public String[] getReplacements() {
        return new String[] {
                "{PRICE}", NumberFormat.suffixFormat(getCost()),
                "{NAME}", getName()
        };
    }

    public void setName(String name) {
        _name = name;
    }

    public void setCost(double cost) {
        _cost = cost;
    }

    public void setDisplayItem(ItemStack displayItem) {
        _displayItem = displayItem;
    }

    public void setPermissions(List<String> permissions) {
        _permissions = permissions;
    }

    public IDataHolder getData(IDataHolder data) {
        data.set("UUID", _uuid, Serializer.UUID);
        data.set("Name", _name);
        data.set("Cost", _cost);
        data.set("Item", _displayItem, Serializer.ITEMSTACK);
        data.setStringList("Permissions", _permissions);

        return data;
    }
}