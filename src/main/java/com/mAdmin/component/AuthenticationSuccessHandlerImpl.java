package com.mAdmin.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.faces.annotation.RequestParameterMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    
    private Log logger = LogFactory.getLog(this.getClass());

    
    private GrantedAuthority grantedAuthorities;

    
    private final RequestCache requestCache = new HttpSessionRequestCache();

    
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        handle(request, response, authentication, savedRequest);
        clearAuthenticationAttributes(request);
    }

    
    private void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
                        SavedRequest savedRequest) throws IOException {

        String targetUrl = determineTargetUrl(request, authentication, savedRequest);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    
    @RequestParameterMap
    protected String determineTargetUrl(HttpServletRequest request, Authentication authentication,
            SavedRequest savedRequest) {

        Collection<? extends GrantedAuthority> authoritiesTemp = authentication.getAuthorities();
        Set<String> authorities = new HashSet<>();
        authoritiesTemp.forEach(at -> authorities.add(at.getAuthority()));

        if (savedRequest == null) {

            return loginByRoles(authorities, request);

        } else if (savedRequest.getParameterMap().containsKey("redirect")) {

            return savedRequest.getRedirectUrl();

        } else {

            return loginByRoles(authorities, request);

        }
    }

    
    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    
    private String loginByRoles(Set<String> authorities, HttpServletRequest request) {

        if ((authorities.contains("ROLE_SADMIN"))) {
            return "/admin/seasons";
        } else if (authorities.contains("ROLE_TEACHER")) {
            return "/teacher/groups";
        } else if (authorities.contains("ROLE_CLIENT")) {
            String url = (String) request.getSession().getAttribute("redirectUrlAfterLoginSuccess");
            if (url != null) {
                request.getSession().removeAttribute("redirectUrlAfterLoginSuccess");
                return url;
            }
            return "/client/attendees";
        } else {
            throw new IllegalStateException();
        }
    }

    
    public Log getLogger() {
        return logger;
    }

    
    public void setLogger(Log logger) {
        this.logger = logger;
    }

}
