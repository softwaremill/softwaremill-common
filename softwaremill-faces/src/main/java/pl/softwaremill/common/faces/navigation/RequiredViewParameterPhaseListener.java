package pl.softwaremill.common.faces.navigation;

import pl.softwaremill.common.cdi.navigation.NavBase;
import pl.softwaremill.common.cdi.util.BeanInject;
import pl.softwaremill.common.faces.messages.FacesMessages;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewMetadata;
import java.io.IOException;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RequiredViewParameterPhaseListener implements PhaseListener {
    public void beforePhase(PhaseEvent event) {
        // Getting the current view id
        FacesContext ctx = event.getFacesContext();   
        if (ctx.isPostback()) {
            return;
        }

        String viewId = ctx.getViewRoot().getViewId();

        // Getting the metadata facet
        ViewDeclarationLanguage vdl = ctx.getApplication().getViewHandler().getViewDeclarationLanguage(ctx, viewId);
        ViewMetadata viewMetadata = vdl.getViewMetadata(ctx, viewId);
        UIViewRoot viewRoot = viewMetadata.createMetadataView(ctx);
        UIComponent metadataFacet = viewRoot.getFacet(UIViewRoot.METADATA_FACET_NAME);

        if (metadataFacet != null) {
            // Checking each parameter
            for (UIComponent child : metadataFacet.getChildren()) {
                if (child instanceof UIViewParameter) {
                    UIViewParameter viewParameter = (UIViewParameter) child;
                    // Checking if the parameter is required but has an empty value
                    if (viewParameter.getRequiredMessage() != null && isEmpty(viewParameter.getStringValue(ctx))) {
                        // Adding the message
                        FacesMessages messages = BeanInject.lookup(FacesMessages.class);
                        messages.addEL(viewParameter.getRequiredMessage(), FacesMessage.SEVERITY_ERROR);

                        // Redirecting to the error page
                        NavBase nav = BeanInject.lookup(NavBase.class);
                        String url = ctx.getApplication().getViewHandler().getActionURL(ctx, nav.getError().s());

                        try {
                            ctx.getExternalContext().redirect(ctx.getExternalContext().encodeActionURL(url));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        ctx.responseComplete();

                        return;
                    }
                }
            }
        }
    }

    public void afterPhase(PhaseEvent event) { }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
