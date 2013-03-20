package pl.softwaremill.common.faces.i18n;

import pl.softwaremill.common.cdi.util.BeanInject;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class CurrentLocaleViewHandler extends ViewHandler {
    private ViewHandler delegate;

    public CurrentLocaleViewHandler(ViewHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public Locale calculateLocale(FacesContext facesContext) {
        CurrentLocale currentLocale = BeanInject.lookup(CurrentLocale.class);
        Locale storedCurrentLocale = currentLocale.getCurrentLocale();
        if (storedCurrentLocale == null) {
            storedCurrentLocale = delegate.calculateLocale(facesContext);
            currentLocale.setCurrentLocale(storedCurrentLocale);
        }

        return storedCurrentLocale;
    }

    @Override
    public String calculateCharacterEncoding(FacesContext context) {
        return delegate.calculateCharacterEncoding(context);
    }

    @Override
    public String calculateRenderKitId(FacesContext facesContext) {
        return delegate.calculateRenderKitId(facesContext);
    }

    @Override
    public UIViewRoot createView(FacesContext facesContext, String s) {
        return delegate.createView(facesContext, s);
    }

    @Override
    public String deriveViewId(FacesContext context, String rawViewId) {
        return delegate.deriveViewId(context, rawViewId);
    }

    @Override
    public String getActionURL(FacesContext facesContext, String s) {
        return delegate.getActionURL(facesContext, s);
    }

    @Override
    public String getResourceURL(FacesContext facesContext, String s) {
        return delegate.getResourceURL(facesContext, s);
    }

    @Override
    public String getRedirectURL(FacesContext context, String viewId, Map<String, List<String>> parameters, boolean includeViewParams) {
        return delegate.getRedirectURL(context, viewId, parameters, includeViewParams);
    }

    @Override
    public String getBookmarkableURL(FacesContext context, String viewId, Map<String, List<String>> parameters, boolean includeViewParams) {
        return delegate.getBookmarkableURL(context, viewId, parameters, includeViewParams);
    }

    @Override
    public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
        return delegate.getViewDeclarationLanguage(context, viewId);
    }

    @Override
    public void initView(FacesContext context) throws FacesException {
        delegate.initView(context);
    }

    @Override
    public void renderView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException, FacesException {
        delegate.renderView(facesContext, uiViewRoot);
    }

    @Override
    public UIViewRoot restoreView(FacesContext facesContext, String s) {
        return delegate.restoreView(facesContext, s);
    }

    @Override
    public void writeState(FacesContext facesContext) throws IOException {
        delegate.writeState(facesContext);
    }
}
