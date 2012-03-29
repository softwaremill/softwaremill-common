package pl.softwaremill.common.dbtest;

import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.ats.internal.arjuna.objectstore.VolatileStore;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform;

import javax.transaction.TransactionManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * User: szimano
 */
public class DBTestJtaBootstrap {
    public static TransactionManager updateConfigAndCreateTM(Map configValues) {
        BeanPopulator
                .getDefaultInstance(ObjectStoreEnvironmentBean.class)
                .setObjectStoreType(VolatileStore.class.getName());

        BeanPopulator
                .getNamedInstance(ObjectStoreEnvironmentBean.class, "communicationStore")
                .setObjectStoreType(VolatileStore.class.getName());

        BeanPopulator
                .getNamedInstance(ObjectStoreEnvironmentBean.class, "stateStore")
                .setObjectStoreType(VolatileStore.class.getName());

        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

        StandardXADataSource dataSource = new StandardXADataSource();
        dataSource.setTransactionManager(com.arjuna.ats.jta.TransactionManager.transactionManager());
        try {
            dataSource.setDriverName(configValues.get(Environment.DRIVER).toString());
        } catch (SQLException e) {
            throw new RuntimeException("Unable to set DataSource JDBC driver name", e);
        }
        dataSource.setUrl(configValues.get(Environment.URL).toString());
        dataSource.setUser(configValues.get(Environment.USER).toString());

        configValues.put(AvailableSettings.JTA_PLATFORM, new JBossStandAloneJtaPlatform());
        configValues.put(Environment.CONNECTION_PROVIDER, DatasourceConnectionProviderImpl.class.getName());
        configValues.put(Environment.DATASOURCE, dataSource);

        return transactionManager;
    }
}
