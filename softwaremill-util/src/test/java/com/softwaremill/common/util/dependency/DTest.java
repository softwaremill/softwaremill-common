package com.softwaremill.common.util.dependency;

import org.testng.annotations.Test;

import java.util.concurrent.Callable;

import static org.fest.assertions.Assertions.*;
import static org.testng.Assert.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class DTest {
    public static class LoginInfo {
        private final String login;

        LoginInfo(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }

    @Test
    public void testAnotherThreadSetsDependency() throws InterruptedException {
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                try {
                    D.withDependencies(new LoginInfo("user1"), new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Thread.sleep(1000);
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // Running r1 - the thread will sleep
        Thread thread1 = new Thread(r1);
        thread1.start();
        Thread.sleep(200);

        // The dependency shouldn't be available
        assertCannotInjectLoginInfo();

        // Cleanup
        thread1.join();
    }

    @Test
    public void testMultithreadedWithDependencies() throws InterruptedException {
        final boolean[] r1ok = {true};
        final boolean[] r2ok = {true};

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                try {
                    D.withDependencies(new LoginInfo("user1"), new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            checkUser();
                            Thread.sleep(500);
                            checkUser();
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            private void checkUser() {
                r1ok[0] = D.inject(LoginInfo.class).getLogin().equals("user1");
            }
        };

        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                try {
                    D.withDependencies(new LoginInfo("user2"), new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            checkUser();
                            Thread.sleep(500);
                            checkUser();
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            private void checkUser() {
                r2ok[0] = D.inject(LoginInfo.class).getLogin().equals("user2");
            }
        };

        // First running r1
        Thread thread1 = new Thread(r1);
        thread1.start();
        Thread.sleep(200);

        // Now running r2
        Thread thread2 = new Thread(r2);
        thread2.start();

        // The interleaving is:
        // 0 - starting r1, checking, sleeping for 500
        // 200 - starting r2, checking, sleeping for 500
        // 500 - checking, stopping r1
        // 700 - checking, stopping r2

        // Waiting for the threads to finish
        thread1.join();
        thread2.join();

        // Checking that they are both ok
        assertThat(r1ok[0]).isTrue();
        assertThat(r2ok[0]).isTrue();
        assertCannotInjectLoginInfo();
    }

    @Test
    public void testWithQualifiers() throws Exception {
        LoginInfo info = new LoginInfo("user");

        LoginInfo injected = D.withDependencies(D.qualifiedDependency(info, D.qualifier(TestQualifier.class)),
                new Callable<LoginInfo>() {
                    @Override
                    public LoginInfo call() throws Exception {
                        return D.inject(LoginInfo.class, D.qualifier(TestQualifier.class));
                    }
                });

        assertThat(injected).isEqualTo(info);
    }

    @Test
    public void testWithQualifiersFromClass() throws Exception {
        LoginInfo info = new LoginInfo("user");

        LoginInfo injected = D.withDependencies(D.qualifiedDependency(info, D.qualifier(TestQualifier.class)),
                new Callable<LoginInfo>() {
                    @Override
                    public LoginInfo call() throws Exception {
                        return D.inject(LoginInfo.class, FooAnnotatedClass.class.getAnnotation(TestQualifier.class));
                    }
                });

        assertThat(injected).isEqualTo(info);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "No dependencies.+")
    public void testWithNoAvailableQualifiers() throws Exception {
        LoginInfo info = new LoginInfo("user");

        LoginInfo injected = D.withDependencies(info,
                new Callable<LoginInfo>() {
                    @Override
                    public LoginInfo call() throws Exception {
                        return D.inject(LoginInfo.class, D.qualifier(TestQualifier.class));
                    }
                });
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "No dependencies.+")
    public void testWithNoWrongQualifiers() throws Exception {
        LoginInfo info = new LoginInfo("user");

        LoginInfo injected = D.withDependencies(D.qualifiedDependency(info, D.qualifier(TestQualifier.class)),
                new Callable<LoginInfo>() {
                    @Override
                    public LoginInfo call() throws Exception {
                        return D.inject(LoginInfo.class, D.qualifier(AnotherTestQualifier.class));
                    }
                });

        System.out.println(injected);
    }

    private void assertCannotInjectLoginInfo() {
        try {
            D.inject(LoginInfo.class);
            fail("Injected a login info!");
        } catch (RuntimeException e) {
            assertThat(e.getMessage().contains("No dependencies"));
        }
    }
}
