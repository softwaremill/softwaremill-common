package pl.softwaremill.common.test.web.email;

import com.dumbster.smtp.SmtpMessage;

import java.util.Iterator;

/**
 * Class represents mock email inbox, with methods manage it.
 *
 * @author maciek
 */
public class EmailInbox {

    private static int REDELIVERY_LIMIT = 10;

    /**
     * Waits for email to appear in inbox. This will happen after email task is downloaded from sqs queue and executed.
     */
    public static void waitForEmailInInbox() throws InterruptedException {
        // Actually gets sent to SQS, might take some time to receive it
        for (int i = 0; i < REDELIVERY_LIMIT; i++) {
            Thread.sleep(7000);
            if (AbstractEmailServerRunner.emailServer.getReceivedEmailSize() > 0) {
                break;
            }
        }
    }

    /**
     * Returns size of inbox
     *
     * @return Number of emails in inbox
     */
    public static int getInboxSize(){
        return AbstractEmailServerRunner.emailServer.getReceivedEmailSize();
    }

    /**
     * Downloads first email from the inbox and removes it from there
     *
     * @return EmailMessage object representing mock email
     */
    public static EmailMessage getFirstEmailFromInbox() {
        Iterator inbox = AbstractEmailServerRunner.emailServer.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) inbox.next();
        inbox.remove();
        return new EmailMessage(email);
    }

    /**
     * Clears mock inbox, removing all email from it.
     * Method should be run after each test, so that next one will have no leftover data
     */
    public static void clearInbox(){
        Iterator inbox = AbstractEmailServerRunner.emailServer.getReceivedEmail();
        while (inbox.hasNext()) {
            inbox.next();
            inbox.remove();
        }
    }

    /**
     *  Checks if the server is initialized and is not stopped
     *
     *  @return true if server is running, false otherwise
     */
    public static boolean isServerRunning() {
        return AbstractEmailServerRunner.emailServer != null && !AbstractEmailServerRunner.emailServer.isStopped();
    }
}
