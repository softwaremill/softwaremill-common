package pl.softwaremill.common.arquillian;

import org.jboss.arquillian.testng.Arquillian;
import org.testng.IHookCallBack;
import org.testng.ITestResult;

public abstract class BetterArquillian extends Arquillian {
    protected void beforeMethodWithDependencies() { }
    protected void afterMethodWithDependencies() { }
    protected void beforeClassWithDependencies() { }

    private boolean beforeClassRun = false;

    @Override
    public void run(final IHookCallBack callback, ITestResult testResult) {
        if (!beforeClassRun) {
            beforeClassRun = true;
            beforeClassWithDependencies();
        }

        super.run(new IHookCallBack() {
            @Override
            public void runTestMethod(ITestResult testResult) {
                beforeMethodWithDependencies();
                try {
                    callback.runTestMethod(testResult);
                } finally {
                    afterMethodWithDependencies();
                }
            }

            @Override
            public Object[] getParameters() {
                return callback.getParameters();
            }
        }, testResult);
    }
}
