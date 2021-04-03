package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.IDataHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PerkDAO implements DAO<Integer, Perk> {

    private final List<Perk> _perks = new LinkedList<>();

    @Nonnull
    @Override
    public Integer create(@Nonnull Perk object) {
        _perks.add(object);
        return _perks.indexOf(object);
    }

    @Override
    public Perk get(@Nonnull Integer id) {
        return id >= _perks.size() || id < 0 ? null : _perks.get(id);
    }

    @Nullable
    @Override
    public Integer getKey(@Nonnull Perk value) {
        int index = _perks.indexOf(value);
        return index < 0 ? null : index;
    }

    @Nonnull
    @Override
    public Collection<Perk> getAll() {
        return Collections.unmodifiableCollection(_perks);
    }

    @Override
    public boolean update(@Nonnull Integer key, @Nonnull Perk object) {
        if(key < 0 || key >= _perks.size()) return false;

        _perks.remove(key.intValue());
        _perks.add(key, object);
        return true;
    }

    @Override
    public boolean delete(@Nonnull Perk object) {
        return _perks.remove(object);
    }

    @Override
    public boolean delete(@Nonnull Integer key) {
        if(key < 0 || key >= _perks.size()) return false;
        _perks.remove(key.intValue());
        return true;
    }

    @Override
    public boolean readAll() {
        if(!_perks.isEmpty())
            _perks.clear();

        IDataHolder data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("perks.json");
        return _perks.addAll(data.getSerializableList("Perks", Perk.class));
    }

    @Override
    public boolean writeAll() {
        IConfiguration data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("perks.json");
        data.setSerializableList("Perks", _perks);
        data.save();
        return true;
    }
}