package pl.softwaremill.common.util;

import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.fest.assertions.Assertions.*;
import static pl.softwaremill.common.util.CDIInjector.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class CDIInjectorTest {
    @Test
    public void testBasicInject() {
        // Given
        InjectInto1 target = new InjectInto1();
        Dep1 dep1 = new Dep1();
        Dep2 dep2 = new Dep2();
        Dep3 dep3 = new Dep3();

        // When
        into(target).inject(dep1, dep2, dep3);

        // Then
        assertThat(target.getDep1()).isSameAs(dep1);
        assertThat(target.getDep2()).isSameAs(dep2);
        assertThat(target.getDep3()).isSameAs(dep3);
    }

    @Test
    public void testInjectSubclass() {
        // Given
        InjectInto1 target = new InjectInto1();
        Dep1 dep1 = new Dep1Sub();

        // When
        into(target).inject(dep1);

        // Then
        assertThat(target.getDep1()).isSameAs(dep1);
    }

    @Test
    public void testMultipleInject() {
        // Given
        InjectInto1 target = new InjectInto1();
        Dep1 dep1 = new Dep1();
        Dep2 dep2 = new Dep2();
        Dep3 dep3 = new Dep3();

        // When
        into(target).inject(dep1).inject(dep2).inject(dep3);

        // Then
        assertThat(target.getDep1()).isSameAs(dep1);
        assertThat(target.getDep2()).isSameAs(dep2);
        assertThat(target.getDep3()).isSameAs(dep3);
    }

    @Test
    public void testInjectWithQualifier() {
        // Given
        InjectInto2 target = new InjectInto2();

        // When
        into(target).injectWithQualifier(KindA.class, "a").injectWithQualifier(KindB.class, "b");

        // Then
        assertThat(target.getStr1()).isEqualTo("a");
        assertThat(target.getStr2()).isEqualTo("b");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testDoubleInjectThrowsException() {
        // Given
        InjectInto1 target = new InjectInto1();

        // When
        into(target).inject(new InjectInto2());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testInjectingUnknownDepThrowsException() {
        // Given
        InjectInto1 target = new InjectInto1();

        // When
        into(target).inject(new Dep1(), new Dep1());
    }

    @Test
    public void testOnlyInjectableFieldsAreSet() {
        // Given
        InjectInto3 target = new InjectInto3();
        Dep1 dep1 = new Dep1();

        // When
        into(target).inject(dep1);

        // Then
        assertThat(target.getDep1()).isSameAs(dep1);
        assertThat(target.getDep1bis()).isNull();
    }

    public static class InjectInto1 {
        @Inject
        private Dep1 dep1;

        @Inject
        private Dep2 dep2;

        @Inject
        private Dep3 dep3;

        public Dep1 getDep1() { return dep1; }
        public Dep2 getDep2() { return dep2; }
        public Dep3 getDep3() { return dep3; }
    }

    public static class InjectInto2 {
        @Inject
        @KindA
        private String str1;

        @Inject
        @KindB
        private String str2;

        public String getStr1() { return str1; }
        public String getStr2() { return str2; }
    }

    public static class InjectInto3 {
        @Inject
        private Dep1 dep1;

        private Dep1 dep1bis;

        public Dep1 getDep1() { return dep1; }
        public Dep1 getDep1bis() { return dep1bis; }
    }

    public static class Dep1 {}
    public static class Dep1Sub extends Dep1 {}
    public static class Dep2 {}
    public static class Dep3 {}

    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface KindA {}

    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface KindB {}
}
