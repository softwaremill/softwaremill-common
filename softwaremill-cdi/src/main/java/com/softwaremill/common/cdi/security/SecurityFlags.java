package com.softwaremill.common.cdi.security;

import javax.enterprise.inject.Model;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * A bean for managing security flags.
 * @author Adam Warski (adam at warski dot org)
 */
@Model
public class SecurityFlags implements Serializable{
    private Set<String> flags = new HashSet<String>();

    public boolean hasFlag(String name) {
        return flags.contains(name);
    }

    public <T> T doWithFlag(String flagName, Callable<T> callable) {
        flags.add(flagName);
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            flags.remove(flagName);
        }
    }
}
