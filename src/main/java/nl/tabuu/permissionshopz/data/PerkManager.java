package nl.tabuu.permissionshopz.data;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public class PerkManager implements Serializable {

    private HashMap<UUID, Perk> _perks;

    public PerkManager(){
        _perks = new HashMap<>();
    }

    public void createPerk(String name, double cost, ItemStack displayItem, String... permissions){
        Perk perk = new Perk(name, cost, displayItem, Arrays.asList(permissions));
        addPerk(perk);
    }

    public void addPerk(Perk perk){
        _perks.put(perk.getUniqueId(), perk);
    }

    public void removePerk(UUID uuid){
        _perks.remove(uuid);
    }

    public Perk getPerk(UUID uuid){
        return _perks.get(uuid);
    }

    public Collection<Perk> getPerks(){
        return _perks.values();
    }

}
