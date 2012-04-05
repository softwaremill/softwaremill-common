package pl.softwaremill.common.dbtest.util;

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitConnectionException;
import org.jboss.arquillian.persistence.event.CleanUpData;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

/**
 * User: szimano
 */
public class DataSetRegisterCleaner {

    @Inject @TestScoped
    private InstanceProducer<DataSetRegister> dataSetRegisterProducer;

    public void cleanDataSetRegister(@Observes(precedence = 1000) EventContext<CleanUpData> context)
    {
        try
        {
            context.proceed();

            // clean the dataSetRegister, because we might get some trash from the previous tests
            dataSetRegisterProducer.set(new DataSetRegister());
        }
        catch (Exception e)
        {
            throw new DBUnitConnectionException("Unable to clean database", e);
        }
    }

}
