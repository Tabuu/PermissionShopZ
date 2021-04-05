package nl.tabuu.permissionshopz.dao;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;
import nl.tabuu.tabuucore.serialization.ISerializer;
import nl.tabuu.tabuucore.serialization.string.AbstractStringSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;

public abstract class MapDAO<K, V extends ISerializable<IDataHolder>> implements DAO<Map<K, V>, K, V> {
    private final Map<K, V> _data;

    protected MapDAO(Map<K, V> data) {
        _data = data;
    }

    @Override
    public Map<K, V> getDataBase() {
        return _data;
    }

    @Override
    public V get(@Nonnull K key) {
        return getDataBase().get(key);
    }

    @Nullable
    @Override
    public K getKey(@Nonnull V value) {
        for(Map.Entry<K, V> entry : getDataBase().entrySet())
            if(Objects.equals(entry.getValue(), value)) return entry.getKey();

        return null;
    }

    @Nonnull
    @Override
    public Collection<V> getAll() {
        return Collections.unmodifiableCollection(getDataBase().values());
    }

    @Override
    public int size() {
        return getDataBase().size();
    }

    @Nonnull
    @Override
    public Collection<V> getAllFiltered(Predicate<V> predicate) {
        return getDataBase().values().stream()
                .filter(predicate)
                .collect(Collector.of(LinkedList<V>::new, List::add, (left, right) -> {
                    left.addAll(right);
                    return left;
                }, Collections::unmodifiableList));
    }

    @Override
    public boolean update(@Nonnull K key, @Nonnull V object) {
        getDataBase().replace(key, object);
        return true;
    }

    @Override
    public boolean delete(@Nonnull V object) {
        return getDataBase().entrySet().removeIf(entry -> Objects.equals(entry.getValue(), object));
    }

    @Override
    public boolean delete(@Nonnull K key) {
        return Objects.nonNull(getDataBase().remove(key));
    }

    @Override
    public boolean deleteIf(Predicate<V> predicate) {
        return getDataBase().entrySet().removeIf(entry -> predicate.test(entry.getValue()));
    }

    @Override
    public ISerializer<K, V> getSerializer() {
        return new ISerializer<K, V>() {
            @Override
            public V serialize(K key) {
                return get(key);
            }

            @Override
            public K deserialize(V value) {
                return getKey(value);
            }

            @Override
            public V serializeArray(K[] keys) {
                throw new UnsupportedOperationException();
            }

            @Override
            public K[] deserializeArray(V value) {
                throw new UnsupportedOperationException();
            }
        };
    }
}