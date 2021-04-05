package nl.tabuu.permissionshopz.dao;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;
import nl.tabuu.tabuucore.serialization.ISerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public interface DAO<D, K, V extends ISerializable<IDataHolder>> {
    @Nonnull K create(@Nonnull V object);
    @Nullable
    V get(@Nonnull K key);
    @Nullable K getKey(@Nonnull V value);
    int size();
    @Nonnull Collection<V> getAll();
    @Nonnull Collection<V> getAllFiltered(Predicate<V> predicate);
    boolean update(@Nonnull K key, @Nonnull V object);
    boolean delete(@Nonnull V object);
    boolean delete(@Nonnull K key);
    boolean deleteIf(Predicate<V> predicate);

    boolean readAll();
    boolean writeAll();
    boolean isGarbage(V object);

    D getDataBase();

    ISerializer<K, V> getSerializer();

    default boolean deleteGarbage() {
        return deleteIf(this::isGarbage);
    }
}