package nl.tabuu.permissionshopz.dao;

import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.ISerializable;
import nl.tabuu.tabuucore.serialization.string.AbstractStringSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;

public abstract class IntegerTreeMapDAO<T extends ISerializable<IDataHolder>> implements DAO<Integer, T> {

    private final StringSerializer _serializer;
    protected final TreeMap<Integer, T> _data = new TreeMap<>();

    public IntegerTreeMapDAO() {
        _serializer = new StringSerializer();
    }

    @Nonnull
    @Override
    public Integer create(@Nonnull T object) {
        int index = _data.isEmpty() ? 0 : (_data.lastKey() + 1);
        _data.put(index, object);
        return index;
    }

    @Override
    public T get(@Nonnull Integer id) {
        return _data.get(id);
    }

    @Nullable
    @Override
    public Integer getKey(@Nonnull T value) {
        for(Map.Entry<Integer, T> entry : _data.entrySet())
            if(Objects.equals(entry.getValue(), value)) return entry.getKey();

        return null;
    }

    @Nonnull
    @Override
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(_data.values());
    }

    @Nonnull
    @Override
    public Collection<T> getAllFiltered(Predicate<T> predicate) {
        return _data.values().stream()
                .filter(predicate)
                .collect(Collector.of(LinkedList<T>::new, List::add, (left, right) -> {
                    left.addAll(right);
                    return left;
                }, Collections::unmodifiableList));
    }

    @Override
    public boolean update(@Nonnull Integer key, @Nonnull T object) {
        _data.replace(key, object);
        return true;
    }

    @Override
    public boolean delete(@Nonnull T object) {
        return _data.entrySet().removeIf(entry -> Objects.equals(entry.getValue(), object));
    }

    @Override
    public boolean delete(@Nonnull Integer key) {
        return Objects.nonNull(_data.remove(key));
    }

    @Override
    public boolean deleteIf(Predicate<T> predicate) {
        return _data.entrySet().removeIf(entry -> predicate.test(entry.getValue()));
    }

    @Override
    public abstract boolean readAll();

    @Override
    public abstract boolean writeAll();

    public StringSerializer getSerializer() {
        return _serializer;
    }

    public class StringSerializer extends AbstractStringSerializer<T> {
        @Override
        public T deserialize(String key) {
            try {
                int index = Integer.parseInt(key);
                return get(index);
            } catch (NumberFormatException exception) {
                return null;
            }
        }

        @Override
        public String serialize(T object) {
            Integer key = getKey(object);
            if(Objects.isNull(key)) return null;

            return String.valueOf(key);
        }
    }
}