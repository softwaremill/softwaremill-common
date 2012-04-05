package pl.softwaremill.common.dbtest.util;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitConnectionException;
import org.jboss.arquillian.persistence.event.CleanUpData;

import javax.naming.Context;

/**
 * User: szimano
 */
public class DBCleaner {

    @Inject
    private Instance<Context> contextInstance;

    public void cleanDatabase(@Observes(precedence = 1000) EventContext<CleanUpData> context)
    {

        try
        {
            context.proceed();

            SchemaExport schemaExport = (SchemaExport) contextInstance.get().lookup("/schemaExport");

            if (schemaExport != null) {
                schemaExport.drop(false, true);
                schemaExport.create(false, true);
            }
        }
        catch (Exception e)
        {
            throw new DBUnitConnectionException("Unable to clean database", e);
        }
    }

}
