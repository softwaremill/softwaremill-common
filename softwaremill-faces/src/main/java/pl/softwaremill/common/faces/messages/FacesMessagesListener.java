package pl.softwaremill.common.faces.messages;

import pl.softwaremill.common.cdi.util.BeanInject;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class FacesMessagesListener implements SystemEventListener {
    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        BeanInject.lookup(FacesMessages.class).beforeRenderResponse();
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return true;
    }
}
