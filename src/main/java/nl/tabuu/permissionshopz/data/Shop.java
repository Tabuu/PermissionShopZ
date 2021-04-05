package nl.tabuu.permissionshopz.data;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.dao.IntegerTreeMapDAO;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Shop implements ISerializable<IDataHolder> {
    private final String _name;
    private String _description;

    private boolean
            _displayUnavailablePerks,
            _displayUnlockedPerks;

    private final List<Perk> _contents;

    public Shop(String name, String description, boolean displayUnavailablePerks, boolean displayUnlockedPerks, List<Perk> contents) {
        _name = name;
        _description = description;
        _displayUnavailablePerks = displayUnavailablePerks;
        _displayUnlockedPerks = displayUnlockedPerks;
        _contents = new LinkedList<>(contents);
    }

    public Shop(String name, String description) {
        this(
                name,
                description,
                true,
                true,
                Collections.emptyList()
        );
    }

    private Shop(IDataHolder data) {
        this(
                data.getString("Name"),
                data.getString("Description"),
                data.getBoolean("DisplayUnavailablePerks", true),
                data.getBoolean("DisplayUnlockedPerks", true),
                data.getList("Contents", getPerkSerializer())
        );
    }

    public boolean add(Perk perk) {
        return _contents.add(perk);
    }

    public boolean remove(Perk perk) {
        return _contents.remove(perk);
    }

    public Collection<Perk> getContents() {
        return Collections.unmodifiableCollection(_contents);
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public boolean displayUnavailablePerks() {
        return _displayUnavailablePerks;
    }

    public void setDisplayUnavailablePerks(boolean displayUnavailablePerks) {
        _displayUnavailablePerks = displayUnavailablePerks;
    }

    public boolean displayUnlockedPerks() {
        return _displayUnlockedPerks;
    }

    public void setDisplayUnlockedPerks(boolean displayUnlockedPerks) {
        _displayUnlockedPerks = displayUnlockedPerks;
    }

    @Override
    public IDataHolder serialize(IDataHolder data) {
        data.set("Name", getName());
        data.set("Description", getDescription());
        data.set("DisplayUnavailablePerks", displayUnavailablePerks());
        data.set("DisplayUnlockedPerks", displayUnlockedPerks());
        data.setList("Contents", _contents, getPerkSerializer());

        return data;
    }

    private static IntegerTreeMapDAO<Perk>.StringSerializer getPerkSerializer() {
        return PermissionShopZ.getInstance().getPerkDao().getSerializer();
    }
}