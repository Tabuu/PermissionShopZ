package nl.tabuu.permissionshopz.dao;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;

public abstract class StringMapDAO<T extends ISerializable<IDataHolder>> extends MapDAO<String, T> {

    protected StringMapDAO() {
        super(new HashMap<>());
    }

    @Override
    public HashMap<String, T> getDataBase() {
        return (HashMap<String, T>) super.getDataBase();
    }

    @Nonnull
    @Override
    public String create(@Nonnull T object) {
        String key = getUniqueKey(object);
        getDataBase().put(key, object);
        return key;
    }

    protected abstract String getUniqueKey(T object);
}