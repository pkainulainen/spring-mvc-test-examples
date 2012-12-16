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

    public SpringSecurityRoleRule() {
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
                setAuthenticationToSecurityContext(roleConfiguration);
            }
            statement.evaluate();
        }

        private void setAuthenticationToSecurityContext(SecurityRole roleConfiguration) {
            UserDetails principal = createPrincipal(roleConfiguration.value());
            Authentication authentication = createAuthentication(principal);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        private UserDetails createPrincipal(String role) {
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
            return new User(USERNAME, PASSWORD, authorities);
        }

        private Authentication createAuthentication(UserDetails principal) {
            return new UsernamePasswordAuthenticationToken(principal, USERNAME, principal.getAuthorities());
        }
    }
}
