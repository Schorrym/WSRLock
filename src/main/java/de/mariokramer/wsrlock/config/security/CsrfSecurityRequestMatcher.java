package de.mariokramer.wsrlock.config.security;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class CsrfSecurityRequestMatcher implements RequestMatcher {

    private Pattern                 allowedMethods      = Pattern.compile("^(POST|GET|HEAD|TRACE|OPTIONS)$");
    private AntPathRequestMatcher   unprotectedMatcher  = new AntPathRequestMatcher("/**/xhr_send**");

    @Override
    public boolean matches(HttpServletRequest request) {
        if (allowedMethods.matcher(request.getMethod()).matches()) {
            return false;
        }

        return !unprotectedMatcher.matches(request);
    }
}