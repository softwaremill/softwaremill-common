package com.softwaremill.common.conf;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ScopedConfig<V> implements Config<String, V> {
    private final Config<String, V> delegate;
    private final String prefix;

    /**
     * Creates a view for the given config, prepending the given prefix to each key. A separating "." is also added.
     */
    public ScopedConfig(Config<String, V> delegate, String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
    }

    private String computeKey(String original) {
        return prefix + "." + original;
    }

    public V get(String key, V defaultValue) {
        return delegate.get(computeKey(key), defaultValue);
    }

    public int getAsInt(String key, int defaultValue) {
        return delegate.getAsInt(computeKey(key), defaultValue);
    }

    public double getAsDouble(String key, double v) {
        return delegate.getAsDouble(computeKey(key), v);
    }

    public boolean getAsBoolean(String key, boolean defaultValue) {
        return delegate.getAsBoolean(computeKey(key), defaultValue);
    }

    public long getAsLong(String key, long defaultValue) {
        return delegate.getAsLong(computeKey(key), defaultValue);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(computeKey(key.toString()));
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegate.get(computeKey(key.toString()));
    }

    public V put(String key, V value) {
        return delegate.put(computeKey(key), value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(computeKey(key.toString()));
    }

    public void putAll(Map<? extends String, ? extends V> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
