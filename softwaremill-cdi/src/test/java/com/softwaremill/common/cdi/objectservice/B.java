package pl.softwaremill.common.cdi.objectservice;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class B extends A {
    private Integer value;

    public B(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
