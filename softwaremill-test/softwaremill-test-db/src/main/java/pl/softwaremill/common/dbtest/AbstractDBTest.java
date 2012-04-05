package pl.softwaremill.common.dbtest;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Environment;
import org.hibernate.ejb.Ejb3Configuration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeSuite;
import pl.softwaremill.common.arquillian.BetterArquillian;
import pl.softwaremill.common.cdi.persistence.EntityManagerFactoryProducer;
import pl.softwaremill.common.dbtest.util.DBTestUserTransaction;
import pl.softwaremill.common.dbtest.util.DbMode;
import pl.softwaremill.common.dbtest.util.InMemoryContextFactoryBuilder;
import pl.softwaremill.common.dbtest.util.UnclosableDataSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

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
 *
 * @author Adam Warski (adam at warski dot org)
 */
public abstract class AbstractDBTest extends BetterArquillian {

    static {
        try {
            NamingManager.setInitialContextFactoryBuilder(new InMemoryContextFactoryBuilder());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    protected EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(AbstractDBTest.class);

    private DbMode compatibilityMode = null;

    protected TransactionManager transactionManager;

    /**
     * Additional Hibernate configuration.
     * E.g. add entities using {@code cfg.addAnnotatedClass(MyEntity.class)}
     */
    protected abstract void configureEntities(Ejb3Configuration cfg);

    @BeforeSuite
    public void setupLog4J() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    public AbstractDBTest() {

        try {

            Ejb3Configuration cfg = new Ejb3Configuration();

            cfg.configure(getPersistanceUnitName(), null);

            // Separate database for each test class
            cfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + this.getClass().getName() +
                    ";DB_CLOSE_DELAY=-1" +
                    addCompatibilityMode());

            transactionManager = DBTestJtaBootstrap.updateConfigAndCreateTM(cfg.getProperties());

            configureEntities(cfg);
            emf = cfg.buildEntityManagerFactory();

            // Setting the EMF so that it's produced correctly
            EntityManagerFactoryProducer.setStaticEntityManagerFactory(emf);

            // bind in JNDI for use in arq-persistance
            DataSource ds = (DataSource) cfg.getHibernateConfiguration().getProperties().get(Environment.DATASOURCE);

            InitialContext initialContext = new InitialContext();

            initialContext.bind("/dataSource", new UnclosableDataSource(ds));
            initialContext.bind("/userTransaction", new DBTestUserTransaction(transactionManager));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Can be overwritten in subclass and can return null
     *
     * @return name of Hibernate XML Configuration file
     */
    protected String getPersistanceUnitName() {
        return "hibernate-test-pu";
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

    public void setCompatibilityMode(DbMode compatibilityMode) {
        this.compatibilityMode = compatibilityMode;
    }

    public DbMode getCompatibilityMode() {
        return compatibilityMode;
    }
}
