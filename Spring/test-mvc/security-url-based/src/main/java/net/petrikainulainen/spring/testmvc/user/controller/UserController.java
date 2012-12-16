package net.petrikainulainen.spring.testmvc.user.controller;

import net.petrikainulainen.spring.testmvc.security.util.SecurityContextUtil;
import net.petrikainulainen.spring.testmvc.user.dto.SecurityRole;
import net.petrikainulainen.spring.testmvc.user.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Petri Kainulainen
 */
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Resource
    private SecurityContextUtil securityContextUtil;

    @RequestMapping(value = "/api/user", method = RequestMethod.GET)
    @ResponseBody
    public UserDTO getLoggedInUser() {
        LOGGER.debug("Getting logged in user.");
        UserDetails principal = securityContextUtil.getPrincipal();
        return createDTO(principal);
    }

    private UserDTO createDTO(UserDetails principal) {
        UserDTO dto = null;
        if (principal != null) {
            String username = principal.getUsername();
            SecurityRole role = getRole(principal.getAuthorities());

            dto = new UserDTO(username, role);
        }

        LOGGER.debug("Created user dto: {}", dto);

        return dto;
    }

    private SecurityRole getRole(Collection<? extends GrantedAuthority> authorities) {
        LOGGER.debug("Getting role from authorities: {}", authorities);

        Iterator<? extends GrantedAuthority> authority = authorities.iterator();
        GrantedAuthority a = authority.next();

        return SecurityRole.valueOf(a.getAuthority());
    }
}
