package pl.softwaremill.common.sqs.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import java.util.Collection;

/**
 * abstract class for a timer manager
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 17, 2010
 */
public abstract class TimerManager {
    private static final Logger LOG = LoggerFactory.getLogger(TimerManager.class);

    @Resource
    TimerService timerService;


    public void startTimer(String id, int interval) {
        // need to make sure all timers are down (e.g. after redeploy)
        destroyTimer(id);

        // Creating a non-persistent timer, so that it won't survive a restart
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerConfig.setInfo(id);
        timerService.createIntervalTimer(interval, interval, timerConfig);

        LOG.info("Timer for " + id + " started.");

    }

    public void destroyTimer(String id) {

        LOG.info("Shutting down all timers...");

        Collection<Timer> timersCollection = timerService.getTimers();

        for (Timer timer : timersCollection) {
            // if (timer.getInfo().equals(id))
            timer.cancel();
            LOG.info("Timer for " + id + " removed.");
        }

        LOG.info("Shut down all timers successfully");

    }
}