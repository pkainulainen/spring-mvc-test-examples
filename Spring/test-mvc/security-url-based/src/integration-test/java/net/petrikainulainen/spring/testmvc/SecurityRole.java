package net.petrikainulainen.spring.testmvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>The <code>SecurityRole</code> annotation is used to configure the role of
 * the logged in user that is used in an integration test. For example, if
 * you want to run an integration test by using an user who has role <code>ROLE_USER</code>,
 * you have annotate the test method by using the following code:</p>
 * <p><code>@SecurityRole("ROLE_USER")</code></p>.
 * <p>This annotation can be used only in the method level.</p>
 * @author Petri Kainulainen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SecurityRole {
   /*
    * The role of the user.
    */
    public String value();
}
