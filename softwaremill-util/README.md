Maven dependency:

    <dependency>
        <groupId>pl.softwaremill.common</groupId>
        <artifactId>softwaremill-util</artifactId>
        <version>[VERSION]</version>
    </dependency>

## CDIInjector

Sets values of fields that are annotated with @Inject basing on types of injected objects.

Mainly a test utility, makes it easier to test beans which use field injection. Instead of manually setting values
of fields (having to provide the field name), it is enough to provide the target of the injection and the objects
to be injected. For example:

    import static pl.softwaremill.common.util.CDIInjector.*;

    @Test
    public void testSomething() {
        // ...

        into(testedBean)
            .inject(mockService1, mockService2)
            .injectWithQualifier(LoggedIn.class, mockUser);

        // ...
    }

Of course the `CDIInjector` doesn't have to be used in tests, it can be used stand-alone also.
