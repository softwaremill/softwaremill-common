package pl.softwaremill.common.cdi.objectservice.auto;

import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;

@ApplicationScoped
public class ExecutionMock implements Serializable{

    Integer myStringExecs = 0;

    Integer stringExecs = 0;
}
