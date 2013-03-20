package com.softwaremill.common.conf;

import java.util.Map;

/**
 * Created by Pawel Stawicki on Feb 24, 2011 12:20:43 PM
 */
public interface Config<K, V> extends Map<K, V> {

    V get(K key, V defaultValue);

    int getAsInt(K key, int defaultValue);

    double getAsDouble(K key, double defaultValue);

    boolean getAsBoolean(K key, boolean defaultValue);

    long getAsLong(K key, long defaultValue);

}
