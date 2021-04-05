package nl.tabuu.permissionshopz.dao;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public interface DAO<K, T extends ISerializable<IDataHolder>> {
    @Nonnull K create(@Nonnull T object);
    @Nullable T get(@Nonnull K key);
    @Nullable K getKey(@Nonnull T value);
    @Nonnull Collection<T> getAll();
    @Nonnull Collection<T> getMatching(Predicate<T> predicate);
    boolean update(@Nonnull K key, @Nonnull T object);
    boolean delete(@Nonnull T object);
    boolean delete(@Nonnull K key);

    boolean readAll();
    boolean writeAll();
}