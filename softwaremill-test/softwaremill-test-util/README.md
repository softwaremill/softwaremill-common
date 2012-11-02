# SoftwareMill Test Util

Maven dependency:

    <dependency>
        <groupId>pl.softwaremill.common</groupId>
        <artifactId>softwaremill-test-util</artifactId>
        <version>70</version>
    </dependency>

## TimeAssertions

Provides assertions like FEST Assert for org.joda.time.DateTime and java.util.Date classes. Examples

    TimeAssertions.assertTime(dateTime).isBefore(laterDateTime);

Comparison methods are isBefore, isBeforeOrAt, isAfter, isAfterOrAt. It works for org.joda.time.DateTime and java.util.Date.
You can freely exchange them, i.e. compare joda to joda, joda to java, java to java etc.

You can also compare joda LocalDateTime, but only to instance of the same class, as it doesn't make sense to compare LocalDateTime
to DateTime or java.util.Date.

Method name is assertTime not assertThat so that you could statically import it. You can't statically import
two methods with the same name.


