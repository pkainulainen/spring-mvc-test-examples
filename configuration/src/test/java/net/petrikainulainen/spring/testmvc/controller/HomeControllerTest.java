package net.petrikainulainen.spring.testmvc.controller;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Petri Kainulainen
 */
public class HomeControllerTest {

    private HomeController controller;

    @Before
    public void setUp() {
        controller = new HomeController();
    }

    @Test
    public void showHomePage() {
        String view = controller.showHomePage();
        assertEquals(HomeController.VIEW_HOME_PAGE, view);
    }
}
