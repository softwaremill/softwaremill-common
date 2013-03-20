package com.softwaremill.common.cdi.persistence;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import java.io.Serializable;

/**
 * The produced entity managers simple delegate to an underlying entity manager, which must be explicitly
 * set before using.
 * @author Adam Warski (adam at warski dot org)
 */
public class MockEntityManagerProducers implements Serializable {
    @Produces @RequestScoped @ReadOnly
    public EntityManagerDelegator getReadOnlyEntityManager() {
        return new EntityManagerDelegator(null);
    }

    @Produces @RequestScoped @Writeable
    public EntityManagerDelegator getWriteableEntityManager() {
        return new EntityManagerDelegator(null);
    }
}
