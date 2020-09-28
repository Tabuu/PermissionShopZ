package nl.tabuu.permissionshopz.data;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PerkManager implements ISerializable<IDataHolder> {

    private Set<Perk> _perks;

    private PerkManager(Collection<Perk> perks) {
        _perks = new LinkedHashSet<>(perks);
    }

    public PerkManager() {
        this(Collections.emptySet());
    }

    public PerkManager(IDataHolder data) {
        this(data.getSerializableList("Perks", Perk.class));
    }

    public void createPerk(String name, double cost, ItemStack displayItem, String... permissions) {
        Perk perk = new Perk(name, cost, displayItem, Arrays.asList(permissions));
        addPerk(perk);
    }

    public void addPerk(Perk perk) {
        _perks.add(perk);
    }

    public void removePerk(Perk perk) {
        _perks.remove(perk);
    }

    public Collection<Perk> getPerks() {
        return Collections.unmodifiableCollection(_perks);
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        data.setSerializableList("Perks", new ArrayList<>(_perks));
        return data;
    }
}