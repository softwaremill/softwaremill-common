# SQS utilities

Maven dependency:

    <dependency>
        <groupId>com.softwaremill.common</groupId>
        <artifactId>softwaremill-sqs</artifactId>
        <version>...</version>
    </dependency>

## Background job executor

A utility for scheduling and executing background jobs. The messaging system used is SQS.

To use, first implement a `Task` and `TaskExecutor` (describing what the job is and how to run it). See `SendEmailTask`
for an example. Instances of `Task` are serialized and sent to the queue. During execution, they are passed to the
appropriate `TaskExecutor`.

Second, create an EJB:

    @Stateless
    @LocalBinding(jndiBinding = "MyAppSQSTaskTimerBean")
    public class MyAppSQSTaskTimerBean extends SQSTaskTimerBean { }

You will need to run the `startTimer()` method on this EJB when your application starts, to kick off the timer. This can
be done for example in a `web.xml` servlet context listener.

You can now schedule jobs by running:

    MyAppSQSTaskTimerBean.scheduleTask(task)

Configuration of SQS access is done through the `sqs.conf` file. Minimal contents of this file:

    AWSAccessKeyId=
    SecretAccessKey=
    queue=

To additionaly send e-mails using the `SendEmailTask`:

    smtpHost=
    smtpPort=
    from=
    encoding=UTF-8