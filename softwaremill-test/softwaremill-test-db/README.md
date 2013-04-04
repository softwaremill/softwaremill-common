# Test-with-DB framework using Arquillian

Maven dependency:

    <dependency>
        <groupId>com.softwaremill.common</groupId>
        <artifactId>softwaremill-test-db</artifactId>
        <version>[VERSION]</version>
    </dependency>

Lets you run tests that use a database. Great for:
* validating entity configuration (annotations, hibernate.cfg.xml) in a test
* testing query syntax (named and regualar queries)
* testing methods which heavily use the `EntityManager` and would be hard to test otherwise.

All test methods are surrounded with a JTA transaction, using a very simple JTA transaction manager.

By default each test has a private in-memory H2 database. However this can be overriden in the `configureEntities`
method.

Moreover, before each test an sql is executed, which can be used to populate the DB with test data. The name of the
sql file is the same as of the test class.

See an example test: [TestOfDBTest](/softwaremill/softwaremill-common/tree/master/softwaremill-test/softwaremill-test-db/src/test/java/com/softwaremill/common/dbtest/).
