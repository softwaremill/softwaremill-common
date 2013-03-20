# Debug utilities

Maven dependency:

    <dependency>
        <groupId>com.softwaremill.common</groupId>
        <artifactId>softwaremill-debug</artifactId>
    </dependency>

Uses code from [Seam forum](http://seamframework.org/Community/SeamPerformanceProblemRewardingWorkaround)
by Tobias Hill.

## Timing interceptor

Times method calls. The results can be displayed using the filter or printed manually using
`TimingResults.dumpCurrent()`.

To enable, annotate desired classes/methdods with `@Timed` and enable the interceptor in `beans.xml`:

    <interceptors>
        <class>com.softwaremill.common.debug.timing.TimingInterceptor</class>
    </interceptors>

## Timing proxy

Times calls to methods on an object. The results can be displayed in the same way as for the interceptor.
To create run:

    SomeInterface proxied = TimingProxy.createFor(interceptedInstanceImplementingSomeInterface, SomeInterface.class);
    // Now all invocations on proxied will be timed

## Timing extension

Automatically adds the interceptor to all beans for which the fully qualified class name contains one of the strings
in the comma-separated list given in the `timed.autoadd` system property.

E.g. if you add `-Dtimed.autoadd=package1,UserFinderImpl` to the Java startup options, the interceptor will be added
to all beans which contain `package1` or `UserFinderImpl` in their fully qualified class name.

Note that you also need to enable the interceptor in your `beans.xml`.

## Timing filter

The filter shows the summarized results of all timings done through the interceptor or through the proxy after a
request completes.

To enable, add to `web.xml`:

    <filter>
        <filter-name>Debug</filter-name>
        <filter-class>com.softwaremill.common.debug.timing.TimingOutputFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>Debug</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>


