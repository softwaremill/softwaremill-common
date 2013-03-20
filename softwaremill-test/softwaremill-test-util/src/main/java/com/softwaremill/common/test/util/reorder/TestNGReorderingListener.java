package com.softwaremill.common.test.util.reorder;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * TestNG Listener modifying order in which test methods are executed in each class. It uses @FirstTest, @LastTest and @TestOrder annotations.
 * <p/>
 * Beware that not all methods and classes can be reordered, only those which don't have any methods depended upon
 * (for more see javadoc for org.testng.IMethodInterceptor)
 *
 * Example ordering in single test class when this listener is used:
 *  - method marked @FirstTest
 *  - method marked @TestOrder(order = 1)
 *  - method marked @TestOrder(order = 2)
 *  - method marked@TestOrder(order = 3)
 *  - test method without @FirstTest / @LastTest / @TestOrder annotations
 *  - method marked @LastTest
 *
 *
 * @author Tomasz Dziurko
 */
public class TestNGReorderingListener implements IMethodInterceptor {

    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {

        ArrayListMultimap<Class, IMethodInstance> methodsInClass = distributeMethodsByClass(methods);

        validate(methodsInClass);

        return reorder(methodsInClass);
    }

    ArrayListMultimap<Class, IMethodInstance> distributeMethodsByClass(List<IMethodInstance> methods) {

        ArrayListMultimap<Class, IMethodInstance> methodsInClass = ArrayListMultimap.create();
        for (IMethodInstance method : methods) {
            methodsInClass.put(method.getMethod().getRealClass(), method);
        }

        return methodsInClass;
    }

    private void validate(ArrayListMultimap<Class, IMethodInstance> methodsInClass) {
        List<String> problemsList = checkForInvalidUsagesInClasses(methodsInClass.keySet());
        problemsList = checkForInvalidUsagesInMethods(problemsList, methodsInClass);

        if (problemsList.size() > 0) {
            throw new IllegalStateException("Invalid usage of " + TestNGReorderingListener.class.getSimpleName() +
                    ". Problems:\n - " + Joiner.on("\n - ").join(problemsList));
        }
    }

    List<String> checkForInvalidUsagesInClasses(Set<Class> classes) {

        List<String> classesAnnotatedWithFirstTest = Lists.newArrayList();

        for (Class clazz : classes) {
            if(clazz.isAnnotationPresent(FirstTest.class)) {
                classesAnnotatedWithFirstTest.add(clazz.getSimpleName());
            }
        }

        List<String> problemsList = Lists.newArrayList();

        if(classesAnnotatedWithFirstTest.size() > 1) {
            problemsList.add("Classes " + Joiner.on(", ").join(classesAnnotatedWithFirstTest) +
                    " are annotated with " + FirstTest.class.getSimpleName() +". Only one class can be annotated this way.");
        }

        return problemsList;
    }

    private List<String> checkForInvalidUsagesInMethods(List<String> problemsList, ArrayListMultimap<Class, IMethodInstance> methodsInClass) {

        for (Class classWithTests : methodsInClass.keySet()) {
            problemsList = checkMethodsInClass(classWithTests, methodsInClass.get(classWithTests), problemsList);
        }

        return problemsList;
    }

    List<String> checkMethodsInClass(Class classWithTests, List<IMethodInstance> methodsInClass,
                                     List<String> problemsList) {
        int firstTestCounter = 0;
        int lastTestCounter = 0;
        int dependsOnMethodsCounter = 0;

        for (IMethodInstance method : methodsInClass) {

            if (isAnnotationPresent(method, FirstTest.class) && isAnnotationPresent(method, LastTest.class)) {
                problemsList.add(classWithTests.getSimpleName() + ": single method can not be annotated with both " +
                        FirstTest.class.getSimpleName() + " and " + LastTest.class.getSimpleName());
            }

            if (isAnnotationPresent(method, FirstTest.class) && isAnnotationPresent(method, TestOrder.class)) {
                problemsList.add(classWithTests.getSimpleName() + ": single method can not be annotated with both " +
                        FirstTest.class.getSimpleName() + " and " + TestOrder.class.getSimpleName());
            }

            if (isAnnotationPresent(method, LastTest.class) && isAnnotationPresent(method, TestOrder.class)) {
                problemsList.add(classWithTests.getSimpleName() + ": single method can not be annotated with both " +
                        LastTest.class.getSimpleName() + " and " + TestOrder.class.getSimpleName());
            }

            if (isAnnotationPresent(method, FirstTest.class)) {
                firstTestCounter++;
            }

            if (isAnnotationPresent(method, LastTest.class)) {
                lastTestCounter++;
            }

            if (method.getMethod().getMethodsDependedUpon().length > 0) {
                dependsOnMethodsCounter++;
            }
        }

        return updateProblemsListIfNeeded(problemsList, classWithTests, firstTestCounter, lastTestCounter, dependsOnMethodsCounter);
    }

    private boolean isAnnotationPresent(IMethodInstance method, Class annotationClass) {
        return method.getMethod().getMethod().isAnnotationPresent(annotationClass);
    }

    private List<String> updateProblemsListIfNeeded(List<String> problemsList, Class classWithTests, int firstTestCounter, int lastTestCounter, int dependsOnMethodsCounter) {
        if (firstTestCounter > 1) {
            problemsList.add(classWithTests.getSimpleName() + ": more than one method marked with annotation " +
                    FirstTest.class.getSimpleName());
        }
        if (lastTestCounter > 1) {
            problemsList.add(classWithTests.getSimpleName() + ": more than one method marked with annotation " +
                    LastTest.class.getSimpleName());
        }
        if (firstTestCounter > 0 && dependsOnMethodsCounter > 0) {
            problemsList.add(classWithTests.getSimpleName() + ": " + FirstTest.class.getSimpleName() +
                    " annotation can not be used in method with dependsOnMethods from @Test");
        }

        return problemsList;
    }

    List<IMethodInstance> reorder(ArrayListMultimap<Class, IMethodInstance> methodsInClass) {

        methodsInClass = reorderMethodsInClasses(methodsInClass);

        ArrayList<IMethodInstance> reorderedMethodsList = reorderClasses(methodsInClass);

        return reorderedMethodsList;
    }

    private ArrayListMultimap<Class, IMethodInstance> reorderMethodsInClasses(ArrayListMultimap<Class, IMethodInstance> methodsInClass) {
        Comparator<IMethodInstance> comparator = createMethodComparator();

        for (Class classWithTests : methodsInClass.keySet()) {
            Collections.sort(methodsInClass.get(classWithTests), comparator);
        }

        return methodsInClass;
    }

    private Comparator<IMethodInstance> createMethodComparator() {
        return new Comparator<IMethodInstance>() {
            public int compare(IMethodInstance methodOne, IMethodInstance methodTwo) {

                if(isAnnotationPresent(methodOne, FirstTest.class)) {
                    return -1;
                }
                else if(isAnnotationPresent(methodTwo, FirstTest.class)) {
                    return 1;
                }
                else if(isAnnotationPresent(methodOne, LastTest.class)) {
                    return 1;
                }
                else if(isAnnotationPresent(methodTwo, LastTest.class)) {
                    return -1;
                }
                else if(isAnnotationPresent(methodOne, TestOrder.class) && isAnnotationPresent(methodTwo, TestOrder.class)) {
                    return  extractOrderFromMethod(methodOne) <
                            extractOrderFromMethod(methodTwo) ? -1 : 1;
                }
                else if(isAnnotationPresent(methodOne, TestOrder.class)) {
                    return -1;
                }
                else if(isAnnotationPresent(methodTwo, TestOrder.class)) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        };
    }

    private int extractOrderFromMethod(IMethodInstance methodOne) {
        return methodOne.getMethod().getMethod().getAnnotation(TestOrder.class).order();
    }

    private ArrayList<IMethodInstance> reorderClasses(ArrayListMultimap<Class, IMethodInstance> methodsInClass) {
        List<Class> classesWithTests = Lists.newArrayList(methodsInClass.keySet());
        Collections.sort(classesWithTests, createClassComparator());

        ArrayList<IMethodInstance> reorderedMethodsList = Lists.newArrayList();

        for(Class clazz : classesWithTests) {
            reorderedMethodsList.addAll(methodsInClass.get(clazz));
        }
        return reorderedMethodsList;
    }

    private Comparator<Class> createClassComparator() {
        return new Comparator<Class>() {
            public int compare(Class classOne, Class classTwo) {

                if(classOne.isAnnotationPresent(FirstTest.class)) {
                    return -1;
                }
                else if(classTwo.isAnnotationPresent(FirstTest.class)) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        };
    }

}
