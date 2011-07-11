package pl.softwaremill.common.cdi.objectservice.auto;

import javax.inject.Inject;

@OSImpl
public class AutoString implements IAuto<String> {

    ExecutionMock execMock;

    @Inject
    public AutoString(ExecutionMock execMock) {
        this.execMock = execMock;
    }

    @Override
    public void doSomething(String bim, String object, Integer bom) {
        execMock.stringExecs += 1;
    }
}
