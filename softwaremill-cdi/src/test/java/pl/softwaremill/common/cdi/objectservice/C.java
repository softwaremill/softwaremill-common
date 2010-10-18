package pl.softwaremill.common.cdi.objectservice;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class C extends A {
    private String value;

    public C(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
