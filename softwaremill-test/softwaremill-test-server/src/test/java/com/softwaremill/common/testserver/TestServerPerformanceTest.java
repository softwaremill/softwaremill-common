package com.softwaremill.common.testserver;

import org.testng.annotations.Test;

import static java.lang.String.format;

public class TestServerPerformanceTest {

    @Test(enabled = false, description = "Run manually when you need it.")
    public void measureHowFastTestServerIs() throws Exception {
        long count = 0;
        double avrCreation = 0;
        double avrStart = 0;
        double avrStop = 0;


        while(true) {
            long beforeCreation = System.currentTimeMillis();
            TestServer server = new TestServer();

            long beforeStart = System.currentTimeMillis();
            server.start();

            long beforeStop = System.currentTimeMillis();
            server.stop();

            long afterStop = System.currentTimeMillis();

            long creation = beforeStart - beforeCreation;
            long start = beforeStop - beforeStart;
            long stop = afterStop - beforeStop;

            count++;

            double prevAvrCreation = avrCreation;
            double prevAvrStart = avrStart;
            double prevAvrStop = avrStop;

            avrCreation = improveAverage(avrCreation, count, creation);
            avrStart = improveAverage(avrStart, count, start);
            avrStop = improveAverage(avrStop, count, stop);

            System.out.println(format("After %s iterations", count));
            System.out.println(format("Average creation: %f", avrCreation));
            System.out.println(format("Average start: %f", avrStart));
            System.out.println(format("Average stop: %f", avrStop));

            if (
                    differsBy1PercentOrLess(prevAvrCreation, avrCreation)
                    && differsBy1PercentOrLess(prevAvrStart, avrStart)
                    && differsBy1PercentOrLess(prevAvrStop, avrStop)
                    && count > 10) {
                break;
            }
        }
    }

    private boolean differsBy1PercentOrLess(double prevAvrCreation, double avrCreation) {
        double onePercent = prevAvrCreation / 100;
        return Math.abs(avrCreation - prevAvrCreation) < onePercent;
    }

    private double improveAverage(double average, long countWithNew, double newValue) {
        return (average * (countWithNew - 1) + newValue) / countWithNew;
    }
}
