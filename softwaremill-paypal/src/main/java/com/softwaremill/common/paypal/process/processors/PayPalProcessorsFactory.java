package com.softwaremill.common.paypal.process.processors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 14:01
 */
public class PayPalProcessorsFactory {

    private Set<Class<? extends PayPalProcessor>> processors = new HashSet<Class<? extends PayPalProcessor>>();

    public PayPalProcessorsFactory(Class<? extends VerifiedPayPalProcessor> processor) {
        this.processors.add(InvalidPayPalProcessor.class);
        this.processors.add(UnknownPayPalProcessor.class);
        this.processors.add(processor);
    }

    public PayPalProcessorsFactory(Set<Class<? extends PayPalProcessor>> processors) {
        this.processors.addAll(processors);
    }

    public List<PayPalProcessor> buildProcessors() {
        List<PayPalProcessor> listOfProcessors = new ArrayList<PayPalProcessor>();
        for (Class<? extends PayPalProcessor> processor : processors) {
            try {
                listOfProcessors.add(createNewInstance(processor));
            } catch (InstantiationException e) {
                //if user needs to have args-constructor should extend this Factory with his own implementation
                throw new RuntimeException("Please check if processor class:" + processor.getCanonicalName() + "\n has non args constructor.", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return listOfProcessors;
    }

    protected <T extends PayPalProcessor> T createNewInstance(Class<T> processorClass) throws IllegalAccessException,
            InstantiationException {
        return processorClass.newInstance();
    }

}
