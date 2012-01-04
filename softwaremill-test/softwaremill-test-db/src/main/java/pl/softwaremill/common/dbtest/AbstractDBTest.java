package pl.softwaremill.common.dbtest;

import com.google.common.io.Resources;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Environment;
import org.hibernate.ejb.AvailableSettings;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.testing.tm.ConnectionProviderImpl;
import org.hibernate.testing.tm.SimpleJtaTransactionManagerImpl;
import org.hibernate.testing.tm.TransactionManagerLookupImpl;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import pl.softwaremill.common.arquillian.BetterArquillian;
import pl.softwaremill.common.cdi.persistence.EntityManagerFactoryProducer;
import pl.softwaremill.common.dbtest.util.DbMode;
import pl.softwaremill.common.dbtest.util.SqlFileResolver;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.*;

/**
 * Extend this class to create tests which have a private database. The persistence classes from cdi-ext are available.
 * A template for a test is:
 * <pre>
 * public class MyTest extends AbstractDBTest {
 *  @Override
 *  public void configureEntities(Ejb3Configuration cfg) {
 *      cfg.addAnnotatedClass(MyEntity.class);
 * }
 * <p/>
 *  @Deployment
 *  public static JavaArchive createTestArchive() {
 *      return new ArchiveConfigurator() {
 *          @Override
 *          protected JavaArchive configureBeans(JavaArchive ar) {
 *              return ar.addPackage(MyBean.class.getPackage());
 *      }.createTestArchive();
 *  }
 * <p/>
 *  @Test
 *  public void mytest() { }
 *  }
 * </pre>
 *
 * @author Adam Warski (adam at warski dot org)
 */
public abstract class AbstractDBTest extends BetterArquillian {

    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(AbstractDBTest.class);

    private DbMode compatibilityMode = null;

    /**
     * Additional Hibernate configuration.
     * E.g. add entities using {@code cfg.addAnnotatedClass(MyEntity.class)}
     */
    protected abstract void configureEntities(Ejb3Configuration cfg);

    /**
     * Loads test data. By default reads the content of an sql file that is named the same as the test class.
     * Override if you want to add additional test data, or suppress default test data loading.
     * <p/>
     * You can use {@link #loadURLContent(javax.persistence.EntityManager, java.net.URL)} to load the specified
     * file.
     *
     * @param em Entity manager which can be used to load data.
     */
    protected void loadTestData(EntityManager em) throws IOException {
        final String sqlFilePath = new SqlFileResolver(this.getClass()).getSqlFilePath();
        try {
            loadURLContent(em, Resources.getResource(sqlFilePath));
        } catch (IllegalArgumentException e) {
            log.info("File for initializing database (" + sqlFilePath + ") not found.");
        }
    }

    /**
     * Executes the SQL statements contained in the content of the given URL.
     */
    protected void loadURLContent(EntityManager em, URL url) throws IOException {
        String queries = Resources.toString(url, Charset.defaultCharset());

        for (String query : divideQueries(queries)) {
            try {
                em.createNativeQuery(query).executeUpdate();
            }
            catch (Exception e) {
                log.error("Problem running query:\n" + query + "\n", e);      
            }
        }
    }

    private List<String> divideQueries(String queries) {
        List<String> queryList = new ArrayList<String>();

        for (String q : queries.split(";")) {
            if (! isBlank(q)) {
                queryList.add(q + ";");
            }
        }

        return queryList;
    }

    @BeforeSuite
    public void setupLog4J() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    @BeforeClass
    public void initDatabase() throws IOException, SystemException, RollbackException, HeuristicRollbackException, HeuristicMixedException, NotSupportedException {
        Ejb3Configuration cfg = new Ejb3Configuration();

        cfg.configure(getHibernateConfigurationFile());

        // Separate database for each test class
        cfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + this.getClass().getName() + addCompatibilityMode());

        cfg.setProperty("connection.provider_class", ConnectionProviderImpl.class.getName());
        cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, TransactionManagerLookupImpl.class.getName());
        cfg.setProperty(AvailableSettings.TRANSACTION_TYPE, "JTA");

        configureEntities(cfg);
        emf = cfg.buildEntityManagerFactory();

        // Setting the EMF so that it's produced correctly
        EntityManagerFactoryProducer.setStaticEntityManagerFactory(emf);

        // Loading the test data for this test
        SimpleJtaTransactionManagerImpl.getInstance().begin();
        EntityManager em = emf.createEntityManager();
        em.joinTransaction();

        loadTestData(em);

        SimpleJtaTransactionManagerImpl.getInstance().commit();
        em.close();
    }

    /**
     * Can be overwritten in subclass and can return null
     *
     * @return name of Hibernate XML Configuration file
     */
    protected String getHibernateConfigurationFile() {
        return "hibernate.test.cfg.xml";
    }

    private String addCompatibilityMode() {
        if (compatibilityMode != null) {
            return ";MODE=" + compatibilityMode.getParameterValue();
        }
        
        return "";
    }

    @AfterClass
    public void cleanupDatabase() {
        emf.close();
    }

    @BeforeMethod
    public void beginTransaction() throws SystemException, NotSupportedException, RollbackException {
        SimpleJtaTransactionManagerImpl.getInstance().begin();

        // There must be at least one sync, otherwise an exception is thrown.
        SimpleJtaTransactionManagerImpl.getInstance().getTransaction().registerSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
            }

            @Override
            public void afterCompletion(int status) {
            }
        });
    }

    @AfterMethod
    public void commitTransaction() throws SystemException, RollbackException, HeuristicRollbackException, HeuristicMixedException {
        SimpleJtaTransactionManagerImpl.getInstance().commit();
    }

    public void setCompatibilityMode(DbMode compatibilityMode) {
        this.compatibilityMode = compatibilityMode;
    }

    public DbMode getCompatibilityMode() {
        return compatibilityMode;
    }
}
