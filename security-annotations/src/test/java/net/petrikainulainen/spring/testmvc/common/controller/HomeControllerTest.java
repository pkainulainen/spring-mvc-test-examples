package net.petrikainulainen.spring.testmvc.common.controller;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

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
        assertEquals(HomeController.HOME_VIEW, view);
    }
}
