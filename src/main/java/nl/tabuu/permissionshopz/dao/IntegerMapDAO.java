package nl.tabuu.permissionshopz.dao;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;

import javax.annotation.Nonnull;
import java.util.TreeMap;

public abstract class IntegerMapDAO<T extends ISerializable<IDataHolder>> extends MapDAO<Integer, T> {

    public IntegerMapDAO() {
        super(new TreeMap<>());
    }

    @Override
    public TreeMap<Integer, T> getDataBase() {
        return (TreeMap<Integer, T>) super.getDataBase();
    }

    @Nonnull
    @Override
    public Integer create(@Nonnull T object) {
        int index = getDataBase().isEmpty() ? 0 : (getDataBase().lastKey() + 1);
        getDataBase().put(index, object);
        return index;
    }
}