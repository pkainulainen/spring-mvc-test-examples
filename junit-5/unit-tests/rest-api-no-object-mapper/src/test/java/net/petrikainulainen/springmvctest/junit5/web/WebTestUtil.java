package net.petrikainulainen.springmvctest.junit5.web;

/**
 * Provides static factory methods which are useful when
 * we are writing automated tests for Spring MVC controllers.
 */
public class WebTestUtil {

    /**
     * Prevent instantion.
     */
    private WebTestUtil() {}

    /**
     * Creates a new string.
     * @param length    The length of the created string.
     * @return  The created string.
     */
    public static String createStringWithLength(int length) {
        StringBuilder testString = new StringBuilder();

        for (int index = 0; index < length; index++) {
            testString.append("a");
        }

        return testString.toString();
    }
}
