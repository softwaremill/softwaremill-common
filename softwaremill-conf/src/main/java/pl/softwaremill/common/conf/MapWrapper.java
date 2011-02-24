package pl.softwaremill.common.conf;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Pawel Stawicki on Feb 24, 2011 12:22:36 PM
 */
public class MapWrapper implements Config<String, String> {

    private Map<String, String> delegate;

    public MapWrapper(Map<String, String> map) {

        this.delegate = map;
    }

    @Override
    public String get(String key, String defaultValue) {
        final String fromMap = delegate.get(key);
        return fromMap != null ? fromMap : defaultValue;
    }

    @Override
    public int getAsInt(String key, int defaultValue) {
        final String fromMap = delegate.get(key);
        return fromMap != null ? Integer.parseInt(fromMap) : defaultValue;
    }

    @Override
    public double getAsDouble(String key, double defaultValue) {
        final String fromMap = delegate.get(key);
        return fromMap != null ? Double.parseDouble(fromMap) : defaultValue;
    }

    @Override
    public boolean getAsBoolean(String key, boolean defaultValue) {
        final String fromMap = delegate.get(key);
        return fromMap != null ? Boolean.parseBoolean(fromMap) : defaultValue;
    }

    @Override
    public long getAsLong(String key, long defaultValue) {
        final String fromMap = delegate.get(key);
        return fromMap != null ? Long.parseLong(fromMap) : defaultValue;
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
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return delegate.get(key);
    }

    @Override
    public String put(String key, String value) {
        return delegate.put(key, value);
    }

    @Override
    public String remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
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
    public Collection<String> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return delegate.entrySet();
    }
}
