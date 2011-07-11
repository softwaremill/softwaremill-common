package pl.softwaremill.common.cdi.objectservice.auto;

import javax.inject.Inject;
import java.net.URL;

@OSImpl
public class AutoURL implements IAuto<URL> {

    ExecutionMock execMock;

    @Inject
    public AutoURL(ExecutionMock execMock) {
        this.execMock = execMock;
    }

    @Override
    public void doSomething(String bim, URL object, Integer bom) {
        execMock.urlExecs += 1;
    }
}
