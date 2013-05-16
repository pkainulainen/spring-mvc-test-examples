package net.petrikainulainen.spring.testmvc.security.authorization;

import net.petrikainulainen.spring.testmvc.user.dto.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Petri Kainulainen
 */
public class TodoPermissionEvaluator implements PermissionEvaluator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoPermissionEvaluator.class);

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        LOGGER.debug("Evaluation permission: {} for domain object: {}", permission, targetDomainObject);
        LOGGER.debug("Received authentication: {}", authentication);

        boolean hasPermission = false;

        if (targetDomainObject.equals("Todo")) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                LOGGER.debug("User is not anonymous. Evaluation permission");
                UserDetails userDetails = (UserDetails) principal;
                String principalRole = getRole(userDetails.getAuthorities());
                if (principalRole.equals(SecurityRole.ROLE_USER.name())) {
                    LOGGER.debug("Principal: {} has permission to perform requested operation", userDetails);
                    hasPermission = true;
                }
            }
            else {
                LOGGER.debug("User is anonymous. Permission denied.");
            }
        }
        else {
            LOGGER.debug("Unknown class: {} for target domain Object: {}", targetDomainObject.getClass(), targetDomainObject);
        }

        LOGGER.debug("Returning: {}", hasPermission);

        return hasPermission;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        LOGGER.debug("Evaluation permission: {} for target type: {}", permission, targetType);
        LOGGER.debug("Received authentication: {}", authentication);

        //Not required here.

        return false;
    }

    /**
     * Returns the role of the logged in user. We assume that each user has only one
     * role.
     * @param authorities
     * @return  The role of the logged in user.
     */
    private String getRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.iterator().next().getAuthority();
    }
}
