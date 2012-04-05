package pl.softwaremill.common.paypal.process.processors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 14:01
 */
public class PayPalProcessorFactory {

    private Set<Class<? extends PayPalProcessor>> processors = new HashSet<Class<? extends PayPalProcessor>>();

    public PayPalProcessorFactory(List<Class<? extends PayPalProcessor>> processors) {
        this.processors.add(InvalidPayPalProcessor.class);
        this.processors.add(UnknownPayPalProcessor.class);
        this.processors.addAll(processors);
    }

    public List<PayPalProcessor> buildProcessors() {
        List<PayPalProcessor> listOfProcessors = new ArrayList<PayPalProcessor>();
        for (Class<? extends PayPalProcessor> processor : processors) {
            try {
                listOfProcessors.add(processor.newInstance());
            } catch (InstantiationException e) {
                new RuntimeException(e);
            } catch (IllegalAccessException e) {
                new RuntimeException(e);
            }
        }
        return listOfProcessors;
    }

}
