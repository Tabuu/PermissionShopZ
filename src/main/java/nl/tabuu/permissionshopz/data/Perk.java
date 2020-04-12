package nl.tabuu.permissionshopz.data;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.util.NumberFormat;
import nl.tabuu.tabuucore.serialization.bytes.Serializer;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Perk implements Serializable {
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

        _uuid = UUID.randomUUID();
        while(PermissionShopZ.getInstance().getPerkManager().getPerk(_uuid) != null)
            _uuid = UUID.randomUUID();
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

    public ItemStack getDisplayItem(){
        return _displayItem.clone();
    }

    public List<String> getPermissions(){
        return _permissions;
    }

    public String[] getReplacements() {
        return new String[] {
                "{PRICE}", NumberFormat.suffixFormat(getCost()),
                "{NAME}", getName()
        };
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(_uuid);
        out.writeObject(_name);
        out.writeObject(_cost);
        out.writeObject(Serializer.ITEMSTACK.serialize(_displayItem));
        out.writeObject(_permissions);

    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        _uuid = (UUID) in.readObject();
        _name = (String) in.readObject();
        _cost = (Double) in.readObject();
        _displayItem = Serializer.ITEMSTACK.deserialize((byte[]) in.readObject())[0];
        _permissions = (List<String>) in.readObject();
    }
}
