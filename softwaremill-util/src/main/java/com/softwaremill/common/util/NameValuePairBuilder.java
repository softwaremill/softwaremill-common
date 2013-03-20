package com.softwaremill.common.util;

import com.google.common.collect.Maps;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.Map;

public class NameValuePairBuilder {

    public static final String NOT_UNIQUE_KEY_ERROR = "Map already contains key with name ";

    private Map<String, String> nameValueMap = Maps.newHashMap();

    public NameValuePairBuilder addPair(String name, String value) {
        if(nameValueMap.containsKey(name))  {
            throw new RuntimeException(NOT_UNIQUE_KEY_ERROR + name);
        }
        nameValueMap.put(name, value);
        return this;
    }

    public NameValuePair[] build() {

        NameValuePair[] array = new NameValuePair[nameValueMap.keySet().size()];

        int index = 0;
        for (String name : nameValueMap.keySet()) {
            String value = nameValueMap.get(name);
            array[index++]= new BasicNameValuePair(name, value);
        }
        return array;
    }
}
