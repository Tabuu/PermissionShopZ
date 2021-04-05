package nl.tabuu.permissionshopz.dao;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;
import nl.tabuu.tabuucore.serialization.ISerializer;
import nl.tabuu.tabuucore.serialization.string.AbstractStringSerializer;
import nl.tabuu.tabuucore.serialization.string.Serializer;

import javax.annotation.Nonnull;
import java.util.Objects;
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

    public AbstractStringSerializer<T> getStringSerializer() {
        return new AbstractStringSerializer<T>() {

            @Override
            public T deserialize(String string) {
                Integer key = Serializer.INTEGER.deserialize(string);
                if(Objects.isNull(key)) return null;

                return getSerializer().serialize(key);
            }

            @Override
            public String serialize(T object) {
                return String.valueOf(getSerializer().deserialize(object));
            }
        };
    }
}