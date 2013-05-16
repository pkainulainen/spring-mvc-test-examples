package net.petrikainulainen.spring.testmvc.security.authorization;

import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import net.petrikainulainen.spring.testmvc.user.dto.SecurityRole;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Petri Kainulainen
 */
public class TodoPermissionEvaluatorTest {

    private static final String DOMAIN_OBJECT_TODO = "Todo";

    private static final String ANONYMOUS = "anonymous";

    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    private static final String PERMISSION_ADD = "add";

    private static final String ROLE_UNKNOWN = "ROLE_UNKNOWN";

    private static final String TARGET_ID = "1";

    private TodoPermissionEvaluator permissionEvaluator;

    @Before
    public void setUp() {
        permissionEvaluator = new TodoPermissionEvaluator();
    }

    @Test
    public void hasPermissionWhenUserIsAnonymous() {
        Authentication anonymous = createAuthenticationForAnonymousUser();
        boolean hasPermission = permissionEvaluator.hasPermission(anonymous, DOMAIN_OBJECT_TODO, PERMISSION_ADD);
        assertFalse(hasPermission);
    }

    @Test
    public void hasPermissionWhenUserIsLoggedInAndTargetDomainObjectIsUnknown() {
        Authentication loggedInUser = createAuthenticationForLoggedInUser(SecurityRole.ROLE_USER.name());
        boolean hasPermission = permissionEvaluator.hasPermission(loggedInUser, new Todo(), PERMISSION_ADD);
        assertFalse(hasPermission);
    }

    @Test
    public void hasPermissionWhenUserIsLoggedInButHasUnknownRole() {
        Authentication loggedInUser = createAuthenticationForLoggedInUser(ROLE_UNKNOWN);
        boolean hasPermission = permissionEvaluator.hasPermission(loggedInUser, DOMAIN_OBJECT_TODO, PERMISSION_ADD);
        assertFalse(hasPermission);
    }

    @Test
    public void hasPermissionWhenUserIsLoggedIn() {
        Authentication loggedInUser = createAuthenticationForLoggedInUser(SecurityRole.ROLE_USER.name());
        boolean hasPermission = permissionEvaluator.hasPermission(loggedInUser, DOMAIN_OBJECT_TODO, PERMISSION_ADD);
        assertTrue(hasPermission);
    }

    @Test
    public void hasPermissionNotImplemented() {
        Authentication loggedInUser = createAuthenticationForLoggedInUser(SecurityRole.ROLE_USER.name());
        boolean hasPermission = permissionEvaluator.hasPermission(loggedInUser, TARGET_ID, DOMAIN_OBJECT_TODO, PERMISSION_ADD);
        assertFalse(hasPermission);
    }

    private Authentication createAuthenticationForAnonymousUser() {
        List<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
        return new AnonymousAuthenticationToken(ANONYMOUS, ANONYMOUS, authorities);
    }

    private Authentication createAuthenticationForLoggedInUser(String role) {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
        UserDetails principal = createPrincipal(authorities);
        return createAuthentication(principal, authorities);
    }

    private UserDetails createPrincipal(List<GrantedAuthority> authorities ) {
        return new User(USERNAME, PASSWORD, authorities);
    }

    private Authentication createAuthentication(UserDetails principal, List<GrantedAuthority> authorities) {
        return new TestingAuthenticationToken(principal, USERNAME, authorities);
    }
}
