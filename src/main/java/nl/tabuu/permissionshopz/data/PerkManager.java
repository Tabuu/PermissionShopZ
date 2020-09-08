package nl.tabuu.permissionshopz.data;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public class PerkManager {

    private HashMap<UUID, Perk> _perks;

    public PerkManager() {
        _perks = new HashMap<>();
    }

    public PerkManager(IDataHolder data) {
        this();

        for(String key : data.getKeys(false)) {
            IDataHolder perkData = data.getDataSection(key);
            UUID uuid = Serializer.UUID.deserialize(key);
            Perk perk = new Perk(perkData);

            _perks.put(uuid, perk);
        }
    }

    public void createPerk(String name, double cost, ItemStack displayItem, String... permissions) {
        Perk perk = new Perk(name, cost, displayItem, Arrays.asList(permissions));
        addPerk(perk);
    }

    public void addPerk(Perk perk) {
        _perks.put(perk.getUniqueId(), perk);
    }

    public void removePerk(UUID uuid) {
        _perks.remove(uuid);
    }

    public Perk getPerk(UUID uuid) {
        return _perks.get(uuid);
    }

    public Collection<Perk> getPerks() {
        return Collections.unmodifiableCollection(_perks.values());
    }

    public IDataHolder getData(IDataHolder data) {

        for (Map.Entry<UUID, Perk> entry : _perks.entrySet()) {
            String key = Serializer.UUID.serialize(entry.getKey());
            IDataHolder perkData = data.createSection(key);
            entry.getValue().getData(perkData);

            data.setDataSection(key, perkData);
        }

        return data;
    }
}