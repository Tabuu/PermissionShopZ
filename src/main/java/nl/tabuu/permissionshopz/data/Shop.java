package nl.tabuu.permissionshopz.data;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.dao.PerkDAO;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;

import java.util.*;
import java.util.stream.Collector;

public class Shop implements ISerializable<IDataHolder> {
    private final String _name;
    private String _description;

    private boolean
            _displayUnavailablePerks,
            _displayUnlockedPerks;

    private final Set<Integer> _contents;

    public Shop(String name, String description, boolean displayUnavailablePerks, boolean displayUnlockedPerks, List<Integer> contents) {
        _name = name;
        _description = description;
        _displayUnavailablePerks = displayUnavailablePerks;
        _displayUnlockedPerks = displayUnlockedPerks;
        _contents = new LinkedHashSet<>(contents);
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
                data.getList("Contents", getPerkDao().getKeySerializer())
        );
    }

    public boolean add(Perk perk) {
        Integer integer = PermissionShopZ.getInstance().getPerkDao().getKey(perk);
        if(Objects.isNull(integer)) return false;

        return _contents.add(integer);
    }

    public boolean remove(Perk perk) {
        Integer integer = PermissionShopZ.getInstance().getPerkDao().getKey(perk);
        if(Objects.isNull(integer)) return false;

        return _contents.remove(integer);
    }

    public Collection<Perk> getContents() {
        return _contents.stream()
                .map(getPerkDao().getSerializer()::serialize)
                .filter(Objects::nonNull)
                .collect(Collector.of(ArrayList<Perk>::new, List::add, (left, right) -> {
                    left.addAll(right);
                    return left;
                }, Collections::unmodifiableList));
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
        _contents.removeIf(integer -> Objects.isNull(getPerkDao().get(integer)));

        data.set("Name", getName());
        data.set("Description", getDescription());
        data.set("DisplayUnavailablePerks", displayUnavailablePerks());
        data.set("DisplayUnlockedPerks", displayUnlockedPerks());
        data.setList("Contents", new LinkedList<>(_contents), getPerkDao().getKeySerializer());

        return data;
    }

    private static PerkDAO getPerkDao() {
        return PermissionShopZ.getInstance().getPerkDao();
    }
}