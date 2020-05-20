package com.mAdmin.security;

import java.io.IOException;
import java.security.Principal;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;


public class AjaxTimeoutPhaseListener implements PhaseListener {

    
    private static final long serialVersionUID = 1L;

    
    @Override
    public void afterPhase(PhaseEvent event) {
    }

    
    @Override
    public void beforePhase(PhaseEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();
        RequestContext rc = RequestContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ec.getResponse();
        HttpServletRequest request = (HttpServletRequest) ec.getRequest();

        Principal user = request.getUserPrincipal();

        if (user == null && ec.getSession(false) == null) {
            
            


            if (ec.isResponseCommitted()) {
                
                return;
            }

            try {

                if (
                        ((rc != null && PrimeFaces.current().isAjaxRequest())
                                || (fc != null && fc.getPartialViewContext().isPartialRequest()))
                        && fc.getResponseWriter() == null
                        && fc.getRenderKit() == null) {

                    response.setCharacterEncoding(request.getCharacterEncoding());

                    RenderKitFactory factory = (RenderKitFactory)
                            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);

                    RenderKit renderKit = factory.getRenderKit(fc,
                            fc.getApplication().getViewHandler().calculateRenderKitId(fc));

                    ResponseWriter responseWriter =
                            renderKit.createResponseWriter(
                                    response.getWriter(), null, request.getCharacterEncoding());
                    fc.setResponseWriter(responseWriter);

                    ec.redirect(ec.getRequestContextPath() + "/login");
                }

            } catch (IOException e) {
                System.out.println("Redirect to the specified page failed");
                throw new FacesException(e);
            }
        } else {
            return; 
        }
    }


    
    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

}
