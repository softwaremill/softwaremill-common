package com.softwaremill.common.test.web.email;

import com.dumbster.smtp.SmtpMessage;
import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class represents mock email inbox, with methods manage it.
 *
 * @author maciek
 */
public class EmailInbox {

    private static int REDELIVERY_LIMIT = 10;

    /**
     * Waits for first email to appear in inbox.
     *
     * When using sqs and timer this will happen after email task is downloaded from queue and executed.
     */
    public static void waitForEmailInInbox() throws InterruptedException {
        // Wait till email is received by mock smtp
        for (int i = 0; i < REDELIVERY_LIMIT; i++) {
            Thread.sleep(7000);
            if (AbstractEmailServerRunner.emailServer.getReceivedEmailSize() > 0) {
                break;
            }
        }
    }

    /**
     * Waits for specific email to appear in inbox. With given subject.
     *
     * When using sqs and timer this will happen after email task is downloaded from queue and executed.
     */
    public static void waitForEmailInInbox(final String subject) throws InterruptedException {
        waitForEmailInInbox(getSubjectPredicate(subject));
    }

    /**
     * Waits for specific email to appear in inbox. With given subject and ToEmail.
     *
     * When using sqs and timer this will happen after email task is downloaded from queue and executed.
     */
    public static void waitForEmailInInbox(final String subject, final String toEmail) throws InterruptedException {
        waitForEmailInInbox(getSubjectAndToEmailPredicate(subject, toEmail));
    }

    /**
     * Waits for specific email to appear in inbox, based on a Predicate
     *
     * When using sqs and timer this will happen after email task is downloaded from queue and executed.
     */
    private static void waitForEmailInInbox(Predicate<SmtpMessage> predicate) throws InterruptedException {
        // Wait till email is received by mock smtp
        DELIVER_CHECK: for (int i = 0; i < REDELIVERY_LIMIT; i++) {
            Thread.sleep(7000);
            if (AbstractEmailServerRunner.emailServer.getReceivedEmailSize() > 0) {
                Iterator inbox = AbstractEmailServerRunner.emailServer.getReceivedEmail();
                SmtpMessage email;
                while(inbox.hasNext()) {
                    email = (SmtpMessage) inbox.next();
                    if(predicate.apply(email)){
                        break DELIVER_CHECK;
                    }
                }
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
     * @return EmailMessage object representing mock email or null if no email found
     */
    public static EmailMessage getFirstEmailFromInbox() {
        Iterator inbox = AbstractEmailServerRunner.emailServer.getReceivedEmail();
        if (inbox.hasNext()) {
            SmtpMessage email = (SmtpMessage) inbox.next();
            inbox.remove();
            return new EmailMessage(email);
        } else {
            return null;
        }
    }

    /**
     * Downloads latest email from the inbox with given subject and removes it from there.
     * Also removes any older emails with given subject.
     *
     * @return EmailMessage object representing mock email
     */
    public static EmailMessage getLatestEmailBySubject(String subject) {
        return getLatestEmailByData(getSubjectPredicate(subject));
    }

    /**
     * Downloads latest email from the inbox with given subject and TO email and removes it from there
     * Also removes any older emails with given subject and TO email.
     *
     * @return EmailMessage object representing mock email
     */
    public static EmailMessage getLatestEmailBySubjectAndToEmail(String subject, String toEmail) {
        return getLatestEmailByData(getSubjectAndToEmailPredicate(subject ,toEmail));
    }

    /**
     * Downloads latest email from the inbox with given predicate (e.g. certain subject or other headers).
     * Also removes any older emails with given predicate.
     *
     * @return EmailMessage object representing mock email
     */
    private static EmailMessage getLatestEmailByData(Predicate<SmtpMessage> predicate) {
        List<SmtpMessage> foundEmails = new ArrayList<SmtpMessage>();
        Iterator inbox = AbstractEmailServerRunner.emailServer.getReceivedEmail();
        SmtpMessage email;
        while (inbox.hasNext()) {
            email = (SmtpMessage) inbox.next();
            if(predicate.apply(email)){
                foundEmails.add(email);
                inbox.remove();
            }
        }

        if(foundEmails.size() > 0) {
            return new EmailMessage(foundEmails.get(foundEmails.size()-1));
        } else {
            return null;
        }
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

    private static Predicate<SmtpMessage> getSubjectPredicate(final String subject) {
        return new Predicate<SmtpMessage>() {
			@Override
			public boolean apply(SmtpMessage email) {
				return email.getHeaderValue("Subject").equals(subject);
			}
		};
    }

    private static Predicate<SmtpMessage> getSubjectAndToEmailPredicate(final String subject, final String toEmail) {
        return new Predicate<SmtpMessage>() {
			@Override
			public boolean apply(SmtpMessage email) {
                return email.getHeaderValue("Subject").equals(subject) && email.getHeaderValue("To").equals(toEmail);
			}
		};
    }
}
