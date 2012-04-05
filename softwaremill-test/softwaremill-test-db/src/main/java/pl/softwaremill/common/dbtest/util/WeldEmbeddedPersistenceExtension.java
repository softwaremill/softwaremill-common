package pl.softwaremill.common.dbtest.util;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.persistence.container.CommandServiceProducer;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitDataHandler;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitDataStateLogger;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitPersistenceTestLifecycleHandler;
import org.jboss.arquillian.persistence.lifecycle.CustomScriptsAroundTestExecutor;
import org.jboss.arquillian.persistence.lifecycle.DataScriptsHandler;
import org.jboss.arquillian.persistence.lifecycle.DatasetHandler;
import org.jboss.arquillian.persistence.lifecycle.ErrorCollectorHandler;
import org.jboss.arquillian.persistence.lifecycle.PersistenceTestHandler;
import org.jboss.arquillian.persistence.lifecycle.TransactionHandler;
import org.jboss.arquillian.persistence.transaction.TransactionalWrapper;

/**
 * User: szimano
 */
public class WeldEmbeddedPersistenceExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(InMemoryContextProducer.class);

        registerTestLifecycleHandlers(builder);
        registerDBUnitHandlers(builder);

        builder.observer(CommandServiceProducer.class)
               .observer(TransactionalWrapper.class);
    }

    private void registerDBUnitHandlers(ExtensionBuilder builder) {
        builder.observer(DBUnitDataHandler.class)
                .observer(DBUnitPersistenceTestLifecycleHandler.class)
                .observer(DBUnitDataStateLogger.class)
                .observer(DataSetRegisterCleaner.class);
    }

    private void registerTestLifecycleHandlers(ExtensionBuilder builder) {
        builder.observer(PersistenceTestHandler.class)
                .observer(CustomScriptsAroundTestExecutor.class)
                .observer(DatasetHandler.class)
                .observer(DataScriptsHandler.class)
                .observer(ErrorCollectorHandler.class)
                .observer(TransactionHandler.class);
    }
}
