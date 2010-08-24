package pl.softwaremill.common.sqs.email;

import javax.ejb.Local;
import javax.ejb.Timer;

/**
 * Local interface for email sender
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 24, 2010
 */
@Local
public interface SQSEmailSender {
    public void startTimer(int interval);

    public void timeout(Timer timer);

    public void destroyTimer();
}
