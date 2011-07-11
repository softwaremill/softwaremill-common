# CDI extensions

Maven dependency:

    <dependency>
        <groupId>pl.softwaremill.common</groupId>
        <artifactId>softwaremill-cdi</artifactId>
        <version>9</version>
    </dependency>

## Stackable security interceptors

Defines the `@Secure` annotation, which takes an EL expression. The expression must evaluate to true, otherwise
access won't be granted. It can also be used as a meta-annotation together with `@SecureBinding`. In case several
`@Secure` annotations apply to a method, all of the must be true. E.g.:

    @SecureBinding
    @Secure("#{loggedInUser.administrator}")
    public @interface AdministratorOnly {
    }

    public class SecureBean {
        @AdministratorOnly
        @Secure("#{additionalSecurityCheck}")
        public void doSecret() { ... }
    }

The values of parameters may be used in the security-checking expression by mapping them with `@SecureVar`.

There's also the `@SecureResult` annotation for checking post-conditions. The result is available under the "result"
EL variable.

Security checks can be temporarily by-passed by setting flags. To allow access to a method when a flag is set, use
the `@AllowWithFlag` annotation. To run code in the privileged, "flagged" mode do:

    @Inject
    private SecurityFlags securityFlags;

    securityFlags.doWithFlag("flagName", new Callable<Void>() {
        @Override
        public Void call() throws Exception {
            return someBean.doWork();
        }
    });

To use, add to your beans.xml the following:

    <interceptors>
        <class>pl.softwaremill.common.cdi.security.SecurityInterceptor</class>
        <class>pl.softwaremill.common.cdi.security.SecurityResultInterceptor</class>
    </interceptors>

Blog links:
* http://www.warski.org/blog/?p=197
* http://www.warski.org/blog/?p=211

## Injectable EL Evaluator

Usage:

    @Inject
    private ELEvaluator elEvaluator;

    void someMethod() {
        // ...
        Integer result = elEvaluator.evaluate("#{testParam1 + 10 + testParam2}", Integer.class, params);
        // ...
    }

The evaluator is a request-scoped bean. It can be injected and used both when a web request is active, and when
not (e.g. during an MDB invocation). The third parameter is optional and is a map of parameters, which will be put
in the EL context for the duration of the evaluation.

## Object services

### Adam's flavor

Object services can be used to "extend" a class hierarchy with methods. If there is a bean implementing some interface
for each class in a hierarchy, using the extension it is possible to obtain a bean corresponding to a given object; the
resolution is performed run-time.

For example, if we have the type hierarchy:
    abstract class A
    class B extends A
    class C extends A

And corresponding services:
    interface TestService<T extends A> extends OS<T> { void someMethod(); }
    class TestServiceB implements TestService<B> { void someMethod() { ... } }
    class TestServiceC implements TestService<C> { void someMethod() { ... } }

The extension registeres object service provider beans:

    @Inject
    OSP<A, TestService<A>> testService;

    void test() {
        // Will invoke someMethod in TestServiceB
        testService.f(new B()).someMethod()

        // Will invoke someMethod in TestServiceC
        testService.f(new C()).someMethod()
    }

The serviced object (B or C in the above example) is set on the services using the `setServiced()` method, when the
service is obtained. On each invocation of `f()`, a new service instance is created.

### Tomek's flavor

Similarly like with Adam's implementation there's one interface for the service and a number of services implementing it for different object types to be served. Resolution, like above, is performed run-time.

With the same class hierarchy 
    abstract class A
    class B extends A
    class C extends A

The implementation is:
    @OS
    interface TestService<T extends A> { void someMethod(T object); }
    @OSImpl
    class TestServiceB implements TestService<B> { void someMethod(B object) { ... } }
    @OSImpl
    class TestServiceC implements TestService<C> { void someMethod(C object) { ... } }

The interface is always annotated with @OS and has to have one parametrized type. All methods have to use this type once and all implementations have to be annotated with @OSImpl.

The extension registeres object service provider beans:

    @Inject
    TestService testService;

    void test() {
        // Will invoke someMethod in TestServiceB
        testService.someMethod(new B());

        // Will invoke someMethod in TestServiceC
        testService.someMethod(new C());
    }

If the implementing service needs to be implemented directly @OSImpl has to be used

    @Inject @OSImpl TestServiceC;

## Static BeanInject

Use `BeanInject.lookup` to obtain the current instance of a bean of the given class and qualifiers / with the given name.

## Config extension

You can configure a bean using a properties file. The properties file must be in the same package as the bean.

By Gaving King, see http://in.relation.to/13053.lace.

## Writeable & read only entity managers

You can inject a read-only and writeable entity managers, to control when entities are written. To read entities, use:

    @Inject
    private @ReadOnly EntityManager readOnlyEntityManager;

To write entities, use the EntityWriter bean:

    @Inject
    private EntityWriter entityWriter;

Any entities passed there must implement the Identifiable interface. The passed entity will be written, and properly
removed and reloaded in the read only EM.

For this to work properly, all relations in all writeable entities must have DETACH cascade enabled.

## Transaction interceptor

Use the `@Transactional` annotation to surround a method call with a transaction. Useful if the call is not in the
scope of a JSF request, and the TX isn't managed by the container (e.g. remoting call).

To enable, add to beans.xml:

    <interceptors>
        <class>pl.softwaremill.common.cdi.transaction.TransactionalInterceptor</class>
    </interceptors>

## TransactionTimeout interceptor

Use the `@TransactionTimeout(timeout = SECONDS)` on a method or type to prolong a transaction
 timeout that is used on an annotated method.

To enable, add to beans.xml:

    <interceptors>
        <class>pl.softwaremill.common.cdi.transaction.TransactionTimeoutInterceptor</class>
    </interceptors>

## Autofactories, or assisted inject implementation for CDI

Assisted inject originates from Guice: http://code.google.com/p/google-guice/wiki/AssistedInject

Using autofactories can save you some time writing simple factories which inject beans and pass them in the
constructor to create the target bean.

For example if you have:

    interface PriceCalculator {
        int getPriceAfterDiscounts();

        interface Factory {
            PriceCalculator create(Product product);
        }
    }

and an implementation (`PriceCalculatorImpl`) which calculates the discount basing on an instance of a Discounts bean,
instead of writing an implementation of the Factory yourself, you can just do:

    @CreatedWith(PriceCalculator.Factory.class)
    public class PriceCalculatorImpl implements PriceCalculator {
        private final Discounts discounts;
        private final Product product;

        @Inject
        public PriceCalculatorImpl(Discounts discounts, @FactoryParameter Product product) {
            this.discounts = discounts;
            this.product = product;
        }

        int getPriceAfterDiscounts() {
            return product.getPrice() - discounts.getNormalDiscount();
        }
    }

Note the usage of the annotations:
- `@CreatedWith` specifies the factory interface, for which an implementation will be created. The interface
should have only one method (later referred to as the factory method)
- `@FactoryParameter` specifies that the annotated constructor parameter corresponds to a parameter of the same class
in the factory method
- `@Inject` specifies that other parameters should be injected from the context

You can then use the factory as any other bean:

    public class Test {
        @Inject
        private PriceCalculator.Factory priceCalculatorFactory;

        public void test() {
            assertThat(priceCalculatorFactory.create(new Product(100)).getPriceAfterDiscounts()).isEqualTo(90);
        }
    }

Alternatively, if you don't want to mix dependencies and factory parameters in the constructor, you can use field
or setter injection:

    @CreatedWith(PriceCalculator.Factory.class)
    public class PriceCalculatorImpl implements PriceCalculator {
        @Inject
        private Discounts discounts;
        
        private final Product product;

        public PriceCalculatorImpl(Product product) {
            this.product = product;
        }

        int getPriceAfterDiscounts() {
            return product.getPrice() - discounts.getNormalDiscount();
        }
    }

The `@Inject` annotation on the constructor and the `@FactoryParameter` annotations aren't needed in this case.