package com.softwaremill.common.sqs.task;

import javax.ejb.Local;
import javax.ejb.Timer;

/**
 * Local interface for the task timer.
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 24, 2010
 */
@Local
public interface SQSTaskTimer {
    public void startTimer(int interval);

    public void timeout(Timer timer);

    public void destroyTimer();
}
