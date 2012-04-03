package pl.softwaremill.common.test.util.reorder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.testng.IMethodInstance;
import org.testng.ITestClass;
import org.testng.ITestNGMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for TestNGReorderingListener
 *
 * @author Tomasz Dziurko
 */
public class TestNGReorderingListenerTest {


    private TestNGReorderingListener listener = new TestNGReorderingListener();

    @Test
    public void shouldFindNoErrorsInValidMethodsList() throws Exception {

        // given
        List<String> problemsList = Lists.newArrayList();
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(true, false));
        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodAnnotatedWith(false, true));

        // when
        problemsList = listener.checkMethodsInClass(this.getClass(), methods, problemsList);

        // then
        assertThat(problemsList).isEmpty();
    }

    @Test
    public void shouldFindNoErrorsInNoAnnotatedMethodsList() throws Exception {

        // given
        List<String> problemsList = Lists.newArrayList();
        List<IMethodInstance> methods = Lists.newArrayList();

        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodAnnotatedWith(false, false));

        // when
        problemsList = listener.checkMethodsInClass(this.getClass(), methods, problemsList);

        // then
        assertThat(problemsList).isEmpty();
    }

    @Test
    public void shouldFindNoErrorsInDependantMethodsListWithoutAnnotations() throws Exception {

        // given
        List<String> problemsList = Lists.newArrayList();
        List<IMethodInstance> methods = Lists.newArrayList();

        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodDependantUponOtherMethods());

        // when
        problemsList = listener.checkMethodsInClass(this.getClass(), methods, problemsList);

        // then
        assertThat(problemsList).isEmpty();
    }

    @Test
    public void shouldNotAllowForMultipleFirstTestAnnotations() throws Exception {

        // given
        List<String> problemsList = Lists.newArrayList();
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(true, false));
        methods.add(mockIMethodAnnotatedWith(true, false));

        // when
        problemsList = listener.checkMethodsInClass(this.getClass(), methods, problemsList);

        // then
        assertThat(problemsList.size()).isEqualTo(1);
        assertThat(problemsList.get(0)).contains("more than one method marked with annotation " + FirstTest.class.getSimpleName());
    }

    @Test
    public void shouldNotAllowForMultipleLastTestAnnotations() throws Exception {

        // given
        List<String> problemsList = Lists.newArrayList();
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(false, true));
        methods.add(mockIMethodAnnotatedWith(false, true));

        // when
        problemsList = listener.checkMethodsInClass(this.getClass(), methods, problemsList);

        // then
        assertThat(problemsList.size()).isEqualTo(1);
        assertThat(problemsList.get(0)).contains("more than one method marked with annotation " + LastTest.class.getSimpleName());
    }

    @Test
    public void shouldNotAllowForMethodAnnotatedWithFirstAndLastAnnotation() throws Exception {

        // given
        List<String> problemsList = Lists.newArrayList();
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(true, true));

        // when
        problemsList = listener.checkMethodsInClass(this.getClass(), methods, problemsList);

        // then
        assertThat(problemsList.size()).isEqualTo(1);
        assertThat(problemsList.get(0)).contains("single method can not be annotated with both " +
                FirstTest.class.getSimpleName() + " and " + LastTest.class.getSimpleName());
    }

    @Test
    public void shouldNotAllowForMethodAnnotatedWithFirstAndTestOrderAnnotation() throws Exception {

        // given
        List<String> problemsList = Lists.newArrayList();
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(true, false, true));

        // when
        problemsList = listener.checkMethodsInClass(this.getClass(), methods, problemsList);

        // then
        assertThat(problemsList.size()).isEqualTo(1);
        assertThat(problemsList.get(0)).contains("single method can not be annotated with both " +
                FirstTest.class.getSimpleName() + " and " + TestOrder.class.getSimpleName());
    }

    @Test
    public void shouldNotAllowForMethodAnnotatedWithLastAndTestOrderAnnotation() throws Exception {

        // given
        List<String> problemsList = Lists.newArrayList();
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(false, true, true));

        // when
        problemsList = listener.checkMethodsInClass(this.getClass(), methods, problemsList);

        // then
        assertThat(problemsList.size()).isEqualTo(1);
        assertThat(problemsList.get(0)).contains("single method can not be annotated with both " +
                LastTest.class.getSimpleName() + " and " + TestOrder.class.getSimpleName());
    }

    @Test
    public void shouldNotAllowForDependantMethodAndMethodWithFirstTestInTheSameClass() throws Exception {

        // given
        List<String> problemsList = Lists.newArrayList();
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(true, false));
        methods.add(mockIMethodDependantUponOtherMethods());

        // when
        problemsList = listener.checkMethodsInClass(this.getClass(), methods, problemsList);

        // then
        assertThat(problemsList.size()).isEqualTo(1);
        assertThat(problemsList.get(0)).contains(": " + FirstTest.class.getSimpleName() +
                " annotation can not be used in method with dependsOnMethods from @Test");
    }

    @Test
    public void shouldFindNoProblemsInProperClassesList() throws Exception {

        // given
        HashSet<Class> classes = Sets.newHashSet();
        classes.add(String.class);
        classes.add(FirstTestAnnotatedClassNumberOne.class);
        classes.add(ClassWithMethodsToTest.class);

        // when
        List<String> problemsList = listener.checkForInvalidUsagesInClasses(classes);


        // then
        assertThat(problemsList.isEmpty()).isTrue();
    }

    @Test
    public void shouldFindProblemsWithMultipleClassesAnnotatedWithFirstTest() throws Exception {

        // given
        HashSet<Class> classes = Sets.newHashSet();
        classes.add(String.class);
        classes.add(FirstTestAnnotatedClassNumberOne.class);
        classes.add(FirstTestAnnotatedClassNumberTwo.class);
        classes.add(ClassWithMethodsToTest.class);

        // when
        List<String> problemsList = listener.checkForInvalidUsagesInClasses(classes);


        // then
        assertThat(problemsList.size()).isEqualTo(1);
        assertThat(problemsList.get(0)).contains(FirstTestAnnotatedClassNumberOne.class.getSimpleName());
        assertThat(problemsList.get(0)).contains(FirstTestAnnotatedClassNumberTwo.class.getSimpleName());
        assertThat(problemsList.get(0)).contains("Only one class can be annotated this way");
    }

    @Test
    public void shouldNotReorderMethodsWithoutAnnotations() throws Exception {

        // given
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodDependantUponOtherMethods());

        // when
        List<IMethodInstance> reorderedMethods = listener.reorder(listener.distributeMethodsByClass(methods));


        // then
        assertThat(reorderedMethods).onProperty("method.methodName").containsExactly(
                ClassWithMethodsToTest.WITHOUT_ANNOTATIONS,
                ClassWithMethodsToTest.METHOD_DEPENDANT_UPON_OTHER_METHODS);

    }

    @Test
    public void shouldMoveSelectedMethodToTheFirstPlace() throws Exception {

        // given
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodAnnotatedWith(true, false));
        methods.add(mockIMethodAnnotatedWith(false, false));

        // when
        List<IMethodInstance> reorderedMethods = listener.reorder(listener.distributeMethodsByClass(methods));

        // then
        assertThat(reorderedMethods).onProperty("method.methodName").containsExactly(
                ClassWithMethodsToTest.WITH_FIRST_TEST_ANNOTATION,
                ClassWithMethodsToTest.WITHOUT_ANNOTATIONS,
                ClassWithMethodsToTest.WITHOUT_ANNOTATIONS);

    }

    @Test
    public void shouldMoveSelectedMethodToTheLastPlace() throws Exception {

        // given
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodAnnotatedWith(false, true));
        methods.add(mockIMethodAnnotatedWith(false, false));

        // when
        List<IMethodInstance> reorderedMethods = listener.reorder(listener.distributeMethodsByClass(methods));

        // then
        assertThat(reorderedMethods).onProperty("method.methodName").containsExactly(
                ClassWithMethodsToTest.WITHOUT_ANNOTATIONS,
                ClassWithMethodsToTest.WITHOUT_ANNOTATIONS,
                ClassWithMethodsToTest.WITH_LAST_TEST_ANNOTATION);

    }

    @Test
    public void shouldReorderMethods() throws Exception {

        // given
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodWithTestOrderAnnotationWithParamTen());
        methods.add(mockIMethodAnnotatedWith(false, true));
        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodAnnotatedWith(true, false));
        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodWithTestOrderAnnotationWithParamOne());

        // when
        List<IMethodInstance> reorderedMethods = listener.reorder(listener.distributeMethodsByClass(methods));

        // then
        assertThat(reorderedMethods).onProperty("method.methodName").containsExactly(
                ClassWithMethodsToTest.WITH_FIRST_TEST_ANNOTATION,
                ClassWithMethodsToTest.WITH_TEST_ORDER_ONE,
                ClassWithMethodsToTest.WITH_TEST_ORDER_TEN,
                ClassWithMethodsToTest.WITHOUT_ANNOTATIONS,
                ClassWithMethodsToTest.WITHOUT_ANNOTATIONS,
                ClassWithMethodsToTest.WITHOUT_ANNOTATIONS,
                ClassWithMethodsToTest.WITH_LAST_TEST_ANNOTATION);

    }

    @Test
    public void shouldOrderMethodsUsingTestOrderAnnotation() throws Exception {

        // given
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodWithTestOrderAnnotationWithParamTen());
        methods.add(mockIMethodWithTestOrderAnnotationWithParamOne());

        // when
        List<IMethodInstance> reorderedMethods = listener.reorder(listener.distributeMethodsByClass(methods));

        // then
        assertThat(reorderedMethods).onProperty("method.methodName").containsExactly(
                ClassWithMethodsToTest.WITH_TEST_ORDER_ONE,
                ClassWithMethodsToTest.WITH_TEST_ORDER_TEN);

    }

    @Test
    public void shouldPlaceFirstTestAnnotatedClassInFirstPlace() throws Exception {

        // given
        List<IMethodInstance> methods = Lists.newArrayList();
        methods.add(mockIMethodAnnotatedWith(false, false));
        methods.add(mockIMethodAnnotatedWith(FirstTestAnnotatedClassNumberOne.class, false, true));
        methods.add(mockIMethodAnnotatedWith(false, false));

        // when
        List<IMethodInstance> reorderedMethods = listener.reorder(listener.distributeMethodsByClass(methods));

        // then
        assertThat(reorderedMethods).onProperty("method.testClass.realClass.simpleName").containsExactly(
                FirstTestAnnotatedClassNumberOne.class.getSimpleName(),
                ClassWithMethodsToTest.class.getSimpleName(),
                ClassWithMethodsToTest.class.getSimpleName());
    }

    private IMethodInstance mockIMethodAnnotatedWith(boolean firstTestAnnotation, boolean lastTestAnnotation,
                                                     boolean orderAnnotation) throws Exception {
        return mockIMethodAnnotatedWith(ClassWithMethodsToTest.class, firstTestAnnotation,
                lastTestAnnotation, orderAnnotation);
    }

    private IMethodInstance mockIMethodAnnotatedWith(boolean firstTestAnnotation, boolean lastTestAnnotation) throws Exception {
        return mockIMethodAnnotatedWith(ClassWithMethodsToTest.class, firstTestAnnotation, lastTestAnnotation);
    }

    private IMethodInstance mockIMethodAnnotatedWith(Class methodClass, boolean firstTestAnnotation,
                                                     boolean lastTestAnnotation) throws Exception {
        return mockIMethodAnnotatedWith(methodClass, firstTestAnnotation, lastTestAnnotation, false);
    }


    private IMethodInstance mockIMethodAnnotatedWith(Class methodClass, boolean firstTestAnnotation,
                                                     boolean lastTestAnnotation, boolean orderAnnotation) throws Exception {
        IMethodInstance methodInstance = mock(IMethodInstance.class);
        ITestNGMethod testNgMethod = mock(ITestNGMethod.class);
        ITestClass testNgClass = mock(ITestClass.class);

        when(methodInstance.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getTestClass()).thenReturn(testNgClass);
        when(testNgClass.getRealClass()).thenReturn(methodClass);

        String methodName;
        if (firstTestAnnotation && lastTestAnnotation && orderAnnotation == true) {
            methodName = ClassWithMethodsToTest.WITH_TEST_ORDER_ONE_AND_FIRST_AND_LAST_ANNOTATION;
        } else if (firstTestAnnotation && lastTestAnnotation && orderAnnotation == false) {
            methodName = ClassWithMethodsToTest.WITH_FIRST_AND_LAST_ANNOTATION;
        } else if (firstTestAnnotation && orderAnnotation == true) {
            methodName = ClassWithMethodsToTest.WITH_TEST_ORDER_ONE_AND_FIRST_TEST;
        }
        else if (firstTestAnnotation && orderAnnotation == false) {
            methodName = ClassWithMethodsToTest.WITH_FIRST_TEST_ANNOTATION;
        }
        else if (lastTestAnnotation && orderAnnotation == true) {
            methodName = ClassWithMethodsToTest.WITH_TEST_ORDER_ONE_AND_LAST_TEST;
        }
        else if (lastTestAnnotation && orderAnnotation == false) {
            methodName = ClassWithMethodsToTest.WITH_LAST_TEST_ANNOTATION;
        }
        else if(orderAnnotation == true) {
            methodName = ClassWithMethodsToTest.WITH_TEST_ORDER_ONE;
        }
        else {
            methodName = ClassWithMethodsToTest.WITHOUT_ANNOTATIONS;
        }

        mockWithMethod(testNgMethod, methodName);

        when(testNgMethod.getMethodsDependedUpon()).thenReturn(new String[]{});
        return methodInstance;
    }

    private void mockWithMethod(ITestNGMethod testNgMethod, String methodName) throws NoSuchMethodException {
        Method method = ClassWithMethodsToTest.class.getMethod(methodName);
        when(testNgMethod.getMethod()).thenReturn(method);
        when(testNgMethod.getMethodName()).thenReturn(methodName);
    }

    private IMethodInstance mockIMethodDependantUponOtherMethods() throws Exception {
        return mockIMethodDependantUponOtherMethods(ClassWithMethodsToTest.class);
    }

    private IMethodInstance mockIMethodDependantUponOtherMethods(Class methodClass) throws Exception {
        IMethodInstance methodInstance = mock(IMethodInstance.class);
        ITestNGMethod testNgMethod = mock(ITestNGMethod.class);
        ITestClass testNgClass = mock(ITestClass.class);

        when(methodInstance.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getTestClass()).thenReturn(testNgClass);
        when(testNgClass.getRealClass()).thenReturn(methodClass);

        when(testNgMethod.getMethodsDependedUpon()).thenReturn(new String[]{"method1, method2"});


        Method methodWithoutAnnotations = ClassWithMethodsToTest.class.getMethod(ClassWithMethodsToTest.WITHOUT_ANNOTATIONS);
        when(testNgMethod.getMethod()).thenReturn(methodWithoutAnnotations);
        when(testNgMethod.getMethodName()).thenReturn(ClassWithMethodsToTest.METHOD_DEPENDANT_UPON_OTHER_METHODS);


        return methodInstance;
    }

    private IMethodInstance mockIMethodWithTestOrderAnnotationWithParamOne() throws Exception {

        IMethodInstance methodInstance = mock(IMethodInstance.class);
        ITestNGMethod testNgMethod = mock(ITestNGMethod.class);
        ITestClass testNgClass = mock(ITestClass.class);

        when(methodInstance.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getTestClass()).thenReturn(testNgClass);
        when(testNgClass.getRealClass()).thenReturn(ClassWithMethodsToTest.class);

        mockWithMethod(testNgMethod, ClassWithMethodsToTest.WITH_TEST_ORDER_ONE);

        when(testNgMethod.getMethodsDependedUpon()).thenReturn(new String[]{});

        return methodInstance;
    }

    private IMethodInstance mockIMethodWithTestOrderAnnotationWithParamTen() throws Exception {

        IMethodInstance methodInstance = mock(IMethodInstance.class);
        ITestNGMethod testNgMethod = mock(ITestNGMethod.class);
        ITestClass testNgClass = mock(ITestClass.class);

        when(methodInstance.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getTestClass()).thenReturn(testNgClass);
        when(testNgClass.getRealClass()).thenReturn(ClassWithMethodsToTest.class);

        mockWithMethod(testNgMethod, ClassWithMethodsToTest.WITH_TEST_ORDER_TEN);

        when(testNgMethod.getMethodsDependedUpon()).thenReturn(new String[]{});

        return methodInstance;
    }



    /**
     * Hack classes as a workaround to problem with prohibited mocking final classes with Mockito.
     * We use some real Method and Class objects annotated with our tested annotations
     */
    private class ClassWithMethodsToTest {

        public static final String WITH_FIRST_AND_LAST_ANNOTATION = "withFirstAndLastAnnotation";
        public static final String WITH_FIRST_TEST_ANNOTATION = "withFirstTestAnnotation";
        public static final String WITH_LAST_TEST_ANNOTATION = "withLastTestAnnotation";
        public static final String WITHOUT_ANNOTATIONS = "withoutAnnotations";
        public static final String METHOD_DEPENDANT_UPON_OTHER_METHODS = "methodDependantUponOtherMethods";
        public static final String WITH_TEST_ORDER_ONE = "withTestOrderOne";
        public static final String WITH_TEST_ORDER_TEN = "withTestOrderTen";
        public static final String WITH_TEST_ORDER_ONE_AND_FIRST_TEST = "withTestOrderOneAndFirstTest";
        public static final String WITH_TEST_ORDER_ONE_AND_LAST_TEST = "withTestOrderOneAndLastTest";
        public static final String WITH_TEST_ORDER_ONE_AND_FIRST_AND_LAST_ANNOTATION = "withTestOrderOneAndFirstAndLastAnnotation";

        @FirstTest
        public void withFirstTestAnnotation() {

        }

        @LastTest
        public void withLastTestAnnotation() {

        }

        public void withoutAnnotations() {

        }

        @FirstTest
        @LastTest
        public void withFirstAndLastAnnotation() {

        }

        @TestOrder(order = 1)
        public void withTestOrderOne() {

        }

        @TestOrder(order = 10)
        public void withTestOrderTen() {

        }

        @FirstTest
        @TestOrder(order = 1)
        public void withTestOrderOneAndFirstTest() {

        }

        @LastTest
        @TestOrder(order = 1)
        public void withTestOrderOneAndLastTest() {

        }

        @FirstTest
        @LastTest
        @TestOrder(order = 1)
        public void withTestOrderOneAndFirstAndLastAnnotation() {

        }


    }

    @FirstTest
    private class FirstTestAnnotatedClassNumberOne {

    }

    @FirstTest
    private class FirstTestAnnotatedClassNumberTwo {

    }
}
