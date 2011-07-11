package pl.softwaremill.common.cdi.objectservice.auto;

import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;

@ApplicationScoped
public class ExecutionMock implements Serializable{

    Integer urlExecs = 0;

    Integer stringExecs = 0;
}
