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
        MyObject actual = (MyObject) new RichObject(new MyObject(expected, null)).set("superField", expected).unwrap();

        // then
        assertEquals(expected, actual.getSuperField());
    }

    @Test
    public void shouldReturnSuperFieldValue() {
        // given
        Object expected = Integer.valueOf("1");

        // when
        Object actual = new RichObject(new MyObject(expected, null)).get("superField");

        // then
        assertEquals(expected, actual);
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "There are 2 with name 'superField' in this class hierarchy!")
    public void shouldComplainAboutDuplicatedFields() {

        // given
        Object value = Integer.valueOf("1");
        Object secondValue = Integer.valueOf("2");
        RichObject richObject = new RichObject(new MyObjectWithDuplicatedField(value, secondValue));

        // when
        richObject.get("superField");
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

class MyObjectWithDuplicatedField extends MySuperObject {
    private Object superField;

    public MyObjectWithDuplicatedField(Object superField, Object myField) {
        super(superField);
        this.superField = myField;
    }
}
