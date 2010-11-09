package pl.softwaremill.common.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class RichObjectTest {

    @Test
    public void shouldSetupField() {
        // given
        Object expected = Integer.valueOf("1");

        // when
        MyObject actual = (MyObject) new RichObject(new MyObject(null, null)).set("myField", expected).unwrap();

        // then
        assertEquals(expected, actual.getMyField());
    }

    @Test
    public void shouldReturnFieldValue() {
        // given
        Object expected = Integer.valueOf("1");

        // when
        Object actual = new RichObject(new MyObject(null, expected)).get("myField");

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void shouldSetupSuperField() {
        // given
        Object expected = Integer.valueOf("1");

        // when
        MyObject actual = (MyObject) new RichObject(new MyObject(expected, null)).setSuper("superField", expected).unwrap();

        // then
        assertEquals(expected, actual.getSuperField());
    }

    @Test
    public void shouldReturnSuperFieldValue() {
        // given
        Object expected = Integer.valueOf("1");

        // when
        Object actual = new RichObject(new MyObject(expected, null)).getSuper("superField");

        // then
        assertEquals(expected, actual);
    }

}

class MySuperObject {

    private Object superField;

    public MySuperObject(Object superField) {
        this.superField = superField;
    }

    public Object getSuperField() {
        return superField;
    }
}


class MyObject extends MySuperObject {

    private Object myField;

    public MyObject(Object superField, Object myField) {
        super(superField);
        this.myField = myField;
    }

    public Object getMyField() {
        return myField;
    }

}
