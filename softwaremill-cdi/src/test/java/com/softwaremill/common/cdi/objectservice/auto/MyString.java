package pl.softwaremill.common.cdi.objectservice.auto;

/**
 * String wrapper
 */
public class MyString {

    public String someString;

    public MyString(String someString) {
        this.someString = someString;
    }

    public String getSomeString() {
        return someString;
    }
}
