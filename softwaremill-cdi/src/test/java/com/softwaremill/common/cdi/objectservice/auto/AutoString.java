package com.softwaremill.common.cdi.objectservice.auto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;

@OSImpl
@ApplicationScoped
public class AutoString implements IAuto<String>{

    ExecutionMock execMock;

    Integer invCounter = 0;

    @Inject
    public AutoString(ExecutionMock execMock) {
        this.execMock = execMock;
    }

    public AutoString() {
    }


    @Override
    public void doSomething(String bim, String object, Integer bom) {
        execMock.stringExecs += 1;

        invCounter += 1;
    }

    public Integer getInvCounter() {
        return invCounter;
    }

    public void setInvCounter(Integer invCounter) {
        this.invCounter = invCounter;
    }
}
