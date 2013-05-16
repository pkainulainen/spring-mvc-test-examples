package net.petrikainulainen.spring.testmvc.user.controller;

import net.petrikainulainen.spring.testmvc.security.util.SecurityContextUtil;
import net.petrikainulainen.spring.testmvc.user.dto.SecurityRole;
import net.petrikainulainen.spring.testmvc.user.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class UserControllerTest {

    private static final GrantedAuthority AUTHORITY = new SimpleGrantedAuthority(SecurityRole.ROLE_USER.name());
    private static final String PASSWORD = "password";
    private static final String USERNAME = "user";

    private UserController controller;

    private SecurityContextUtil securityContextUtilMock;

    @Before
    public void setUp() {
        controller = new UserController();

        securityContextUtilMock = mock(SecurityContextUtil.class);
        ReflectionTestUtils.setField(controller, "securityContextUtil", securityContextUtilMock);
    }

    @Test
    public void getLoggedInUser() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(AUTHORITY);

        UserDetails principal = new User(USERNAME, PASSWORD, authorities);

        when(securityContextUtilMock.getPrincipal()).thenReturn(principal);

        UserDTO loggedInUser = controller.getLoggedInUser();

        verify(securityContextUtilMock, times(1)).getPrincipal();
        verifyNoMoreInteractions(securityContextUtilMock);

        assertEquals(USERNAME, loggedInUser.getUsername());
        assertEquals(SecurityRole.ROLE_USER, loggedInUser.getRole());
    }

    @Test
    public void getLoggedInUSerWhenUserIsNotLoggedIn() {
        when(securityContextUtilMock.getPrincipal()).thenReturn(null);

        UserDTO loggedInUser = controller.getLoggedInUser();

        verify(securityContextUtilMock, times(1)).getPrincipal();
        verifyNoMoreInteractions(securityContextUtilMock);

        assertNull(loggedInUser);
    }
}
