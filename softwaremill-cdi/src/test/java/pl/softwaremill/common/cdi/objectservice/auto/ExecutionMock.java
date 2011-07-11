package pl.softwaremill.common.cdi.objectservice.auto;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExecutionMock {

    int urlExecs = 0;

    int stringExecs = 0;
}
