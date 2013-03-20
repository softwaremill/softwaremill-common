package pl.softwaremill.common.cdi.objectservice.auto;

import javax.inject.Inject;

@OSImpl
public class AutoMyString implements IAuto<MyString> {

    ExecutionMock execMock;

    @Inject
    public AutoMyString(ExecutionMock execMock) {
        this.execMock = execMock;
    }

    @Override
    public void doSomething(String bim, MyString object, Integer bom) {
        execMock.myStringExecs += 1;
    }
}
