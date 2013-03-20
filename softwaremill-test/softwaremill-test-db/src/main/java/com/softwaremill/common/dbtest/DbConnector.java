package com.softwaremill.common.dbtest;

import org.hibernate.cfg.Environment;
import org.hibernate.ejb.AvailableSettings;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.testing.tm.ConnectionProviderImpl;
import org.hibernate.testing.tm.TransactionManagerLookupImpl;
import com.softwaremill.common.dbtest.util.DbMode;

import javax.persistence.EntityManagerFactory;

/**
 * @author Pawel Stawicki
 * @since 7/27/12 1:15 PM
 */
public class DbConnector {

    private Ejb3Configuration configuration;

    public DbConnector() {
        this("hibernate.test.cfg.xml");
    }

    public DbConnector(String hibernateConfigurationFileResource) {
        createEjb3Configuration(hibernateConfigurationFileResource);
    }

    private void createEjb3Configuration(String hibernateConfigurationFileResource) {
        configuration = new Ejb3Configuration();

        configuration.configure(hibernateConfigurationFileResource);

        // Separate database for each test class
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + this.getClass().getName());

        configuration.setProperty("connection.provider_class", ConnectionProviderImpl.class.getName());
        configuration.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, TransactionManagerLookupImpl.class.getName());
        configuration.setProperty(AvailableSettings.TRANSACTION_TYPE, "RESOURCE_LOCAL");
    }

    public void setCompatibilityMode(DbMode compatibilityMode) {
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + this.getClass().getName() + addCompatibilityMode(compatibilityMode));
    }

    private String addCompatibilityMode(DbMode compatibilityMode) {
        if (compatibilityMode != null) {
            return ";MODE=" + compatibilityMode.getParameterValue();
        }

        return "";
    }

    public Ejb3Configuration getConfiguration() {
        return configuration;
    }

    public EntityManagerFactory createEntityManagerFactory() {
        return configuration.buildEntityManagerFactory();
    }

}
