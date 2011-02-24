package pl.softwaremill.common.conf;

import java.util.Map;

/**
 * Created by Pawel Stawicki on Feb 24, 2011 12:20:43 PM
 */
public interface Config<K, V> extends Map<K, V> {

    V get(K key, V defaultValue);

    int getAsInt(V key, int defaultValue);

    double getAsDouble(V s, double v);

    boolean getAsBoolean(V key, boolean defaultValue);

    long getAsLong(V key, long defaultValue);

}
