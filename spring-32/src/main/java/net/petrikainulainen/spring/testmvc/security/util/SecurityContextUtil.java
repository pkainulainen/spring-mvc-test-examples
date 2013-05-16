package net.petrikainulainen.spring.testmvc.security.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author Petri Kainulainen
 */
@Component
public class SecurityContextUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityContextUtil.class);

    public UserDetails getPrincipal() {
        LOGGER.debug("Getting principal from the security context");

        UserDetails principal = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object currentPrincipal = authentication.getPrincipal();
            if (currentPrincipal instanceof UserDetails) {
                principal = (UserDetails) currentPrincipal;
            }
        }

        return principal;
    }
}
