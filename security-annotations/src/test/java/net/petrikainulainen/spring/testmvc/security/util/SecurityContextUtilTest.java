package net.petrikainulainen.spring.testmvc.security.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class})
public class SecurityContextUtilTest {

    private SecurityContextUtil securityContextUtil;

    @Before
    public void setUp() {
        securityContextUtil = new SecurityContextUtil();
    }

    @Test
    public void getPrincipal() {
        SecurityContext securityContextMock = mock(SecurityContext.class);
        Authentication authenticationMock = mock(Authentication.class);

        PowerMockito.mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContextMock);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);

        UserDetails expectedPrincipal = new User("user", "password", new ArrayList<GrantedAuthority>());
        when(authenticationMock.getPrincipal()).thenReturn(expectedPrincipal);

        UserDetails actualPrincipal = securityContextUtil.getPrincipal();

        PowerMockito.verifyStatic(times(1));
        SecurityContextHolder.getContext();

        verify(securityContextMock, times(1)).getAuthentication();
        verifyNoMoreInteractions(securityContextMock);

        verify(authenticationMock, times(1)).getPrincipal();
        verifyNoMoreInteractions(authenticationMock);

        assertEquals(expectedPrincipal, actualPrincipal);
    }

    @Test
    public void getPrincipalWhenAuthenticationIsNull() {
        SecurityContext securityContextMock = mock(SecurityContext.class);

        PowerMockito.mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContextMock);

        when(securityContextMock.getAuthentication()).thenReturn(null);

        UserDetails principal = securityContextUtil.getPrincipal();

        PowerMockito.verifyStatic(times(1));
        SecurityContextHolder.getContext();

        verify(securityContextMock, times(1)).getAuthentication();
        verifyNoMoreInteractions(securityContextMock);

        assertNull(principal);
    }

    @Test
    public void getPrincipalWhenAuthenticationDoesNotImplementUserDetails() {
        SecurityContext securityContextMock = mock(SecurityContext.class);
        Authentication authenticationMock = mock(Authentication.class);

        PowerMockito.mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContextMock);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(new String(""));

        UserDetails principal = securityContextUtil.getPrincipal();

        PowerMockito.verifyStatic(times(1));
        SecurityContextHolder.getContext();

        verify(securityContextMock, times(1)).getAuthentication();
        verifyNoMoreInteractions(securityContextMock);

        verify(authenticationMock, times(1)).getPrincipal();
        verifyNoMoreInteractions(authenticationMock);

        assertNull(principal);
    }
}
