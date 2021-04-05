package nl.tabuu.permissionshopz.dao;

import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;
import nl.tabuu.tabuucore.serialization.string.AbstractStringSerializer;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;

public interface IConfigurationDAO<K, V extends ISerializable<IDataHolder>> extends DAO<Map<K, V>, K, V> {

    String getConfigurationKey();

    IConfiguration getConfiguration();

    AbstractStringSerializer<K> getKeySerializer();

    @Override
    default boolean readAll() {
        if(!getDataBase().isEmpty())
            getDataBase().clear();

        ParameterizedType parameterTypes = (ParameterizedType) getClass().getGenericSuperclass();
        System.out.println(parameterTypes.getTypeName());
        System.out.println(Arrays.toString(parameterTypes.getActualTypeArguments()));
        Class<V> valueClass = (Class<V>) parameterTypes.getActualTypeArguments()[0];

        getDataBase().putAll(getConfiguration().getSerializableMap(getConfigurationKey(), valueClass, getKeySerializer()));
        return true;
    }

    @Override
    default boolean writeAll() {
        getConfiguration().setSerializableMap(getConfigurationKey(), getDataBase(), getKeySerializer());
        getConfiguration().save();
        return true;
    }
}
