package pl.softwaremill.common.cdi.objectservice.auto;

@OS
public interface IAuto<T> {

    void doSomething(String bom, T object, Integer bim);
}
