package pl.softwaremill.common.debug.timing;

import com.google.common.base.Strings;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.util.RichString;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import java.util.Collections;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class TimingExtension implements Extension {
    private final static Logger log = LoggerFactory.getLogger(TimingExtension.class);
    private final static List<String> autoAddFor;

    static {
        String autoAddForString = System.getProperty("timed.autoadd");
        if (!Strings.isNullOrEmpty(autoAddForString)) {
            autoAddFor = new RichString(autoAddForString).splitByCommaGetNonEmpty();

            log.info("Automatically adding the timing interceptor to all beans containing: " + autoAddFor);
        } else {
            autoAddFor = Collections.emptyList();
        }
    }

    public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> event) {
        if (interceptAnnotatedType(event.getAnnotatedType())) {
            log.info("Adding the timing interceptor for " + event.getAnnotatedType().getJavaClass().getName());
            AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(event.getAnnotatedType());
            builder.addToClass(new TimedImpl());
            event.setAnnotatedType(builder.create());
        }
    }

    private boolean interceptAnnotatedType(AnnotatedType at) {
        if (at.getJavaClass().getEnclosingClass() != null) {
            return false;
        }

        if (at.isAnnotationPresent(Timed.class)) {
            return false;
        }

        String className = at.getJavaClass().getName();
        for (String autoAdd : autoAddFor) {
            if (className.contains(autoAdd)) {
                return true;
            }
        }

        return false;
    }

    private static class TimedImpl extends AnnotationLiteral<Timed> implements Timed {}
}
