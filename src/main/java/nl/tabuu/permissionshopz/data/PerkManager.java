package nl.tabuu.permissionshopz.data;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.configuration.holder.JsonDataHolder;
import nl.tabuu.tabuucore.debug.Debug;
import nl.tabuu.tabuucore.serialization.ISerializable;

import java.util.*;

public class PerkManager implements ISerializable<IDataHolder> {

    private final List<Perk> _perks;

    private PerkManager(Collection<Perk> perks) {
        _perks = new LinkedList<>(perks);
    }

    public PerkManager() {
        this(Collections.emptySet());
    }

    public PerkManager(IDataHolder data) {
        this(data.getSerializableList("Perks", Perk.class));
    }

    public Perk createDefaultPerk() {
        Perk perk = new Perk(new JsonDataHolder()); // Creating a perk based on an empty configuration.
        addPerk(perk);
        return perk;
    }

    public void addPerk(Perk perk) {
        _perks.add(perk);
    }

    public void removePerk(Perk perk) {
        Debug.log(_perks.remove(perk));
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