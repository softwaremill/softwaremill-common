package com.softwaremill.common.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RichList<T> {
    private final List<T> wrapped;

    public RichList(List<T> wrapped) {
        this.wrapped = wrapped;
    }

    public <U> List<U> flatMap(Function<T, List<U>> mappingFunction) {
        List<U> result = new ArrayList<U>();
        for (T element : wrapped) {
            result.addAll(mappingFunction.apply(element));
        }

        return result;
    }

    public List<T> filter(Predicate<T> predicate) {
        List<T> result = new ArrayList<T>();
        for (T element : wrapped) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }

        return result;
    }
}
