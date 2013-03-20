package com.softwaremill.common.cdi.persistence;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Creates a query. Can be used to properly execute bulk update queries using
 * {@link com.softwaremill.common.cdi.persistence.EntityWriter#executeUpdate(QueryCreator)}.
 * @author Adam Warski (adam at warski dot org)
 */
public interface QueryCreator {
    /**
     * Given an entity manager, creates a query to execute.
     * @param em Entity manager 
     * @return Query to execute.
     */
    Query createQuery(EntityManager em);
}
