package com.softwaremill.common.cdi.persistence;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

/**
 * Before most operations on an {@link EntityManager}, calls {@link javax.persistence.EntityManager#joinTransaction()}.
 * @author Adam Warski (adam at warski dot org)
 */
public class EntityManagerTxEnlistDecorator implements EntityManager {
    private EntityManager delegate;

    public EntityManagerTxEnlistDecorator(EntityManager delegate) {
        this.delegate = delegate;
    }

    public void persist(Object entity) {
        delegate.joinTransaction();
        delegate.persist(entity);
    }

    public <T> T merge(T entity) {
        delegate.joinTransaction();
        return delegate.merge(entity);
    }

    public void remove(Object entity) {
        delegate.joinTransaction();
        delegate.remove(entity);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        delegate.joinTransaction();
        return delegate.find(entityClass, primaryKey);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        delegate.joinTransaction();
        return delegate.find(entityClass, primaryKey, properties);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        delegate.joinTransaction();
        return delegate.find(entityClass, primaryKey, lockMode);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        delegate.joinTransaction();
        return delegate.find(entityClass, primaryKey, lockMode, properties);
    }

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        delegate.joinTransaction();
        return delegate.getReference(entityClass, primaryKey);
    }

    public void flush() {
        delegate.joinTransaction();
        delegate.flush();
    }

    public void setFlushMode(FlushModeType flushMode) {
        delegate.joinTransaction();
        delegate.setFlushMode(flushMode);
    }

    public FlushModeType getFlushMode() {
        delegate.joinTransaction();
        return delegate.getFlushMode();
    }

    public void lock(Object entity, LockModeType lockMode) {
        delegate.joinTransaction();
        delegate.lock(entity, lockMode);
    }

    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        delegate.joinTransaction();
        delegate.lock(entity, lockMode, properties);
    }

    public void refresh(Object entity) {
        delegate.joinTransaction();
        delegate.refresh(entity);
    }

    public void refresh(Object entity, Map<String, Object> properties) {
        delegate.joinTransaction();
        delegate.refresh(entity, properties);
    }

    public void refresh(Object entity, LockModeType lockMode) {
        delegate.joinTransaction();
        delegate.refresh(entity, lockMode);
    }

    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        delegate.joinTransaction();
        delegate.refresh(entity, lockMode, properties);
    }

    public void clear() {
        delegate.joinTransaction();
        delegate.clear();
    }

    public void detach(Object entity) {
        delegate.joinTransaction();
        delegate.detach(entity);
    }

    public boolean contains(Object entity) {
        delegate.joinTransaction();
        return delegate.contains(entity);
    }

    public LockModeType getLockMode(Object entity) {
        delegate.joinTransaction();
        return delegate.getLockMode(entity);
    }

    public void setProperty(String propertyName, Object value) {
        delegate.joinTransaction();
        delegate.setProperty(propertyName, value);
    }

    public Map<String, Object> getProperties() {
        delegate.joinTransaction();
        return delegate.getProperties();
    }

    public Query createQuery(String qlString) {
        delegate.joinTransaction();
        return delegate.createQuery(qlString);
    }

    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        delegate.joinTransaction();
        return delegate.createQuery(criteriaQuery);
    }

    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        delegate.joinTransaction();
        return delegate.createQuery(qlString, resultClass);
    }

    public Query createNamedQuery(String name) {
        delegate.joinTransaction();
        return delegate.createNamedQuery(name);
    }

    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        delegate.joinTransaction();
        return delegate.createNamedQuery(name, resultClass);
    }

    public Query createNativeQuery(String sqlString) {
        delegate.joinTransaction();
        return delegate.createNativeQuery(sqlString);
    }

    public Query createNativeQuery(String sqlString, Class resultClass) {
        delegate.joinTransaction();
        return delegate.createNativeQuery(sqlString, resultClass);
    }

    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        delegate.joinTransaction();
        return delegate.createNativeQuery(sqlString, resultSetMapping);
    }

    public void joinTransaction() {
        delegate.joinTransaction();
    }

    public <T> T unwrap(Class<T> cls) {
        delegate.joinTransaction();
        return delegate.unwrap(cls);
    }

    public Object getDelegate() {
        delegate.joinTransaction();
        return delegate.getDelegate();
    }

    public void close() {
        delegate.close();
    }

    public boolean isOpen() {
        return delegate.isOpen();
    }

    public EntityTransaction getTransaction() {
        delegate.joinTransaction();
        return delegate.getTransaction();
    }

    public EntityManagerFactory getEntityManagerFactory() {
        delegate.joinTransaction();
        return delegate.getEntityManagerFactory();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        delegate.joinTransaction();
        return delegate.getCriteriaBuilder();
    }

    public Metamodel getMetamodel() {
        delegate.joinTransaction();
        return delegate.getMetamodel();
    }
}