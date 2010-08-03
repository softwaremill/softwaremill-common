package pl.softwaremill.common.dbtest;

import com.google.common.io.Resources;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Environment;
import org.hibernate.ejb.AvailableSettings;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.test.tm.ConnectionProviderImpl;
import org.hibernate.test.tm.SimpleJtaTransactionManagerImpl;
import org.hibernate.test.tm.TransactionManagerLookupImpl;
import org.jboss.arquillian.testng.Arquillian;
import org.testng.annotations.*;
import pl.softwaremill.cdiext.persistence.EntityManagerFactoryProducer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Extend this class to create tests which have a private database. The persistence classes from cdi-ext are available.
 * A template for a test is:
 * <pre>
 * public class MyTest extends AbstractDBTest {
 *  @Override
 *  public void configureEntities(Ejb3Configuration cfg) {
 *      cfg.addAnnotatedClass(MyEntity.class);
 * }
 *
 *  @Deployment
 *  public static JavaArchive createTestArchive() {
 *      return new ArchiveConfigurator() {
 *          @Override
 *          protected JavaArchive configureBeans(JavaArchive ar) {
 *              return ar.addPackage(MyBean.class.getPackage());
 *      }.createTestArchive();
 *  }
 *
 *  @Test
 *  public void mytest() { }
 *  }
 * </pre>
 * @author Adam Warski (adam at warski dot org)
 */
public abstract class AbstractDBTest extends Arquillian {
    private EntityManagerFactory emf;

    /**
     * Additional Hibernate configuration.
     * E.g. add entities using {@code cfg.addAnnotatedClass(MyEntity.class)}
     */
    protected abstract void configureEntities(Ejb3Configuration cfg);

    /**
     * Loads test data. By default reads the content of an sql file that is named the same as the test class.
     * Override if you want to add additional test data, or suppress default test data loading.
     *
     * You can use {@link #loadURLContent(javax.persistence.EntityManager, java.net.URL)} to load the specified
     * file.
     *
     * @param em Entity manager which can be used to load data.
     */
    protected void loadTestData(EntityManager em) throws IOException {
        loadURLContent(em, Resources.getResource(this.getClass().getSimpleName().toLowerCase() + ".sql"));
    }

    /**
     * Executes the SQL statements contained in the content of the given URL.
     */
    protected void loadURLContent(EntityManager em, URL url) throws IOException {
        String query = Resources.toString(url, Charset.defaultCharset());
        em.createNativeQuery(query).executeUpdate();
    }

    @BeforeSuite
    public void setupLog4J() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    @BeforeClass
    public void initDatabase() throws IOException, SystemException, RollbackException, HeuristicRollbackException, HeuristicMixedException, NotSupportedException {
        Ejb3Configuration cfg = new Ejb3Configuration();

        cfg.configure("hibernate.test.cfg.xml");

        // Separate database for each test class
        cfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + this.getClass().getName());

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

    @AfterClass
    public void cleanupDatabase() {
        emf.close();
    }

    @BeforeMethod
    public void beginTransaction() throws SystemException, NotSupportedException {
        SimpleJtaTransactionManagerImpl.getInstance().begin();
    }

    @AfterMethod
    public void commitTransaction() throws SystemException, RollbackException, HeuristicRollbackException, HeuristicMixedException {
        SimpleJtaTransactionManagerImpl.getInstance().commit();
    }
}
