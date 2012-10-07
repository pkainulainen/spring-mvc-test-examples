package net.petrikainulainen.spring.testmvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>The <code>ApplicationContextSetup</code> annotation is used to configure
 * the application context configuration class or configuration file that is
 * used to create new <code>MockMvc</code> objects.</p>
 *
 * <p>This annotation is given at the class level and all tests of that test class
 * are run by using the provided application context configuration.</p>
 *
 * @author Petri Kainulainen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ApplicationContextSetup {

    /**
     * The application context configuration class.
     */
    public Class configurationClass() default Empty.class;

    /**
     * The location of the application context configuration file.
     */
    public String configurationFile() default "";
}
