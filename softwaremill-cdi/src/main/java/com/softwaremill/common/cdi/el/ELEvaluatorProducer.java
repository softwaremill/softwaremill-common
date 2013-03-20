package pl.softwaremill.common.cdi.el;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ELEvaluatorProducer {
    @Inject
    private BeanManager beanManager;

    @Produces @RequestScoped
    public ELEvaluator getEvaluator() {
        if (FacesContext.getCurrentInstance() != null) {
            return new FacesContextELEvaluator(FacesContext.getCurrentInstance());
        } else {
            return new TemporaryContextELEvaluator(beanManager);
        }
    }
}
