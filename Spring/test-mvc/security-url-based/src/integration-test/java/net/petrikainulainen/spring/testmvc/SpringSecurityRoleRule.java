package net.petrikainulainen.spring.testmvc;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.server.MockMvc;

import java.lang.reflect.Field;
import java.util.List;

/**
 * <p>This rule is used to create <code>Authentication</code> objects and set the created
 * objects in to the <code>SecurityContext</code>.</p>
 * <p>In order to use this rule, you must annotate the test method with the <code>SecurityRule</code>
 * annotation.</p>
 * @author Petri Kainulainen
 */
public class SpringSecurityRoleRule extends ExternalResource {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    private Object test;

    public SpringSecurityRoleRule(Object test) {
        this.test = test;
    }

    @Override
    protected void after() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new SecurityStatement(base, description);
    }

    public class SecurityStatement extends Statement {

        private Description description;
        private Statement statement;

        public SecurityStatement(Statement statement, Description description) {
            this.description = description;
            this.statement = statement;
        }

        @Override
        public void evaluate() throws Throwable {
            SecurityRole roleConfiguration = description.getAnnotation(SecurityRole.class);

            if (roleConfiguration != null) {
                Authentication authentication = createAuthentication(roleConfiguration);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                injectAuthenticationToTest(authentication);
            }
            statement.evaluate();
        }

        private Authentication createAuthentication(SecurityRole roleConfiguration) {
            UserDetails principal = createPrincipal(roleConfiguration.value());
            return createAuthentication(principal);
        }

        private UserDetails createPrincipal(String role) {
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
            return new User(USERNAME, PASSWORD, authorities);
        }

        private Authentication createAuthentication(UserDetails principal) {
            return new UsernamePasswordAuthenticationToken(principal, USERNAME, principal.getAuthorities());
        }

        private void injectAuthenticationToTest(Authentication authentication) {
            Class testClass = description.getTestClass();
            Field[] fields = testClass.getDeclaredFields();
            for (Field currentField: fields) {
                if (currentField.getType().equals(Authentication.class)) {
                    try {
                        currentField.setAccessible(true);
                        currentField.set(test, authentication);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("The MockMvc object could not be injected to field: " + currentField.getName());
                    }
                    break;
                }
            }
        }
    }
}
