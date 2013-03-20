package pl.softwaremill.common.dbtest;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.ejb.AvailableSettings;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.transaction.BTMTransactionManagerLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import pl.softwaremill.common.arquillian.BetterArquillian;
import pl.softwaremill.common.cdi.persistence.EntityManagerFactoryProducer;
import pl.softwaremill.common.dbtest.util.DbMode;
import pl.softwaremill.common.util.dependency.BeanManagerDependencyProvider;
import pl.softwaremill.common.util.dependency.D;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Status;
import java.util.Properties;

/**
 * Allows to create fully integration test with real transactions
 * Take a look on TestTransactionalDBTest
 */
public abstract class TransactionalDBTest extends BetterArquillian {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionalDBTest.class);

    /**
     * Used to configure Transaction Manager
     *
     * - bitronix.uniqueName - name to be used with JNDI to identify given Transaction Manager
     * - bitronix.file - full path to the Bitronix Transaction Manager configuration file
     *                  follow the link for more details
     *                  http://docs.codehaus.org/display/BTM/Configuration2x
     */
    protected static final String BITRONIX_UNIQUE_NAME = "bitronix.uniqueName";
    protected static final String BITRONIX_FILE = "bitronix.file";

    private static final String BITRONIX_DATASOURCE_CLASS = "bitronix.datasource.class";
    private static final String BITRONIX_CONNECTION_USERNAME = "bitronix.connection.username";
    private static final String BITRONIX_CONNECTION_URL = "bitronix.connection.url";

    protected EntityManagerFactory emf;

    @Inject
    private BeanManager bm;

    private BeanManagerDependencyProvider depProvider;
    private UtxDependencyProvider utxProvider;

    @BeforeSuite
    public void beforeSuite() throws Exception {
        setupLog4J();
        callBeforeSuite();
    }

    @AfterSuite
    public void afterSuite() throws Exception {
        callAfterSuite();
    }

    @BeforeClass
    public void beforeClass() throws Exception {
        callBeforeClass();

        Ejb3Configuration cfg = new Ejb3Configuration();
        configureTransactionManager(cfg);
        initTransactionManager(cfg);
        configureDatabase(cfg);
        configureEntities(cfg);
        emf = cfg.buildEntityManagerFactory();
        // Setting the EMF so that it's produced correctly
        EntityManagerFactoryProducer.setStaticEntityManagerFactory(emf);

        initTestData();
    }

    protected void initTransactionManager(Ejb3Configuration cfg) throws Exception{
        String configurationFile = getConfigurationFile();
        if (configurationFile != null) {
            LOG.info("Using [{}] as a configuration file for Bitronix Transaction Manager!", configurationFile);
            TransactionManagerServices.getConfiguration().setResourceConfigurationFilename(configurationFile);
        } else {
            // create InMemory H2 database
            PoolingDataSource xa = new PoolingDataSource();
            xa.setUniqueName(cfg.getProperties().getProperty(BITRONIX_UNIQUE_NAME, "test-" + getClass().getSimpleName()));
            xa.setClassName(cfg.getProperties().getProperty(BITRONIX_DATASOURCE_CLASS, JdbcDataSource.class.getName()));
            xa.setAllowLocalTransactions(true);
            xa.setMaxPoolSize(3);
            xa.setMinPoolSize(1);

            Properties prop = new Properties();
            String url = cfg.getProperties().getProperty(BITRONIX_CONNECTION_URL, "jdbc:h2:mem:" + getClass().getSimpleName());
            if (compatibilityMode() != null) {
                url += ";MODE=" + compatibilityMode();
            }
            prop.setProperty("URL", url);
            prop.setProperty("user", cfg.getProperties().getProperty(BITRONIX_CONNECTION_USERNAME, "sa"));

            xa.setDriverProperties(prop);
            xa.init();

            TransactionManagerServices.getResourceLoader().getResources().put(xa.getUniqueName(), xa);
        }
        TransactionManagerServices.getResourceLoader().init();
    }

    protected String getConfigurationFile() throws Exception {
        return null;
    }

    protected void initTestData() throws Exception {
        EntityManager em = emf.createEntityManager();
        loadTestData(em);
    }

    @AfterClass
    public void afterClass() throws Exception {
        callAfterClass();

        shutdownEmf();
        shutdownTransactionManager();
        D.unregister(depProvider);
        D.unregister(utxProvider);
    }

    @BeforeMethod
    public void beforeMethod() throws Exception {
        callBeforeMethod();
        registerBeanManager();
        registerUtxProvider();
    }

    protected void registerUtxProvider() {
        if (utxProvider == null) {
            utxProvider = new UtxDependencyProvider();
            D.register(utxProvider);
        }
    }

    protected void registerBeanManager() {
        if (depProvider == null) {
            depProvider = new BeanManagerDependencyProvider(bm);
            D.register(depProvider);
        }
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        callAfterMethod();
    }

    /**
     * Override to setup test on BeforeSuite
     */
    protected void callBeforeSuite() throws Exception {
    }

    /**
     * Override to tear down test on AfterSuite
     */
    protected void callAfterSuite() throws Exception {
    }

    /**
     * Override to setup test on BeforeClass
     */
    protected void callBeforeClass() throws Exception {
    }

    /**
     * Override to tear down test on AfterClass
     */
    protected void callAfterClass() throws Exception {
    }

    /**
     * Override to setup test on BeforeMethod
     */
    protected void callBeforeMethod() throws Exception {
    }

    /**
     * Override to tear down test on AfterMethod
     */
    protected void callAfterMethod() throws Exception {
    }

    /**
     * Override to setup Log4j
     */
    protected void setupLog4J() {
        BasicConfigurator.configure();
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
    }

    /**
     * Used to setup TransactionManager
     *
     * @param cfg current H3 configuration
     */
    protected void configureTransactionManager(Ejb3Configuration cfg) {
        cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, BTMTransactionManagerLookup.class.getName());
        cfg.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "jta");
        cfg.setProperty(AvailableSettings.TRANSACTION_TYPE, "JTA");
    }

    protected void configureDatabase(Ejb3Configuration cfg) {
        cfg.setProperty(Environment.HBM2DDL_AUTO, "create");
        cfg.setProperty(Environment.RELEASE_CONNECTIONS, "after_statement");
        cfg.setProperty(Environment.DATASOURCE, "test-" + getClass().getSimpleName());
        cfg.setProperty(Environment.DIALECT, H2Dialect.class.getName());
    }

    protected abstract void configureEntities(Ejb3Configuration cfg);

    protected void shutdownTransactionManager() {
        TransactionManagerServices.getTransactionManager().shutdown();
    }

    protected void shutdownEmf() {
        emf.close();
    }

    protected void beginTransaction() throws Exception {
        BitronixTransactionManager tm = TransactionManagerServices.getTransactionManager();
        if (tm.getStatus() != Status.STATUS_NO_TRANSACTION) {
            throw new RuntimeException("Active transaction in progress! Please commit or roll it back!");
        }
        tm.begin();
    }

    protected void commitTransaction() throws Exception {
        BitronixTransactionManager tm = TransactionManagerServices.getTransactionManager();
        if (tm.getStatus() != Status.STATUS_ACTIVE) {
            throw new RuntimeException("Transaction is not active and cannot be committed! Please start a new transaction first!");
        }
        tm.commit();
    }

    protected void rollbackTransaction() throws Exception {
        BitronixTransactionManager tm = TransactionManagerServices.getTransactionManager();
        if (tm.getStatus() != Status.STATUS_ACTIVE && tm.getStatus() != Status.STATUS_MARKED_ROLLBACK) {
            throw new RuntimeException("Transaction is not active and cannot be rolled back! Please start a new transaction first!");
        }
        tm.rollback();
    }

    /**
     * Override to provide support for H2 compatibility mode
     *
     * @return DbMode to use with H2, null means don't use compatibility mode and act as a standard H2
     */
    protected DbMode compatibilityMode() {
        return null;
    }

    /**
     * Override to load test data into database used for test, the steps are as follow:
     * - beginTransaction()
     * - em.joinTransaction()
     * - ... test data ...
     * - commitTransaction() / rollbackTransaction()
     * - em.close()
     *
     * EM is working with real transactions so you must either commit or rollback active transaction.
     *
     * @param em active EM
     * @throws Exception
     */
    protected abstract void loadTestData(EntityManager em) throws Exception;

}
