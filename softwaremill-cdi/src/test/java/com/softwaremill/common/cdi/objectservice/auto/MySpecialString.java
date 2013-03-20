package com.softwaremill.common.cdi.objectservice.auto;

/**
 * Class that extends MyString for inheritance checks
 */
public class MySpecialString extends MyString{

    public MySpecialString(String someString) {
        super(someString + "ButVerySpecial");
    }
}
