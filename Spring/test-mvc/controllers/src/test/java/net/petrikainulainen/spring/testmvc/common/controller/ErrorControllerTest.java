package net.petrikainulainen.spring.testmvc.common.controller;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Petri Kainulainen
 */
public class ErrorControllerTest {

    private ErrorController controller;

    @Before
    public void setUp() {
        controller = new ErrorController();
    }

    @Test
    public void show404Page() {
        String view = controller.show404Page();
        assertEquals(ErrorController.VIEW_NOT_FOUND, view);
    }

    @Test
    public void showInternalServerErrorPage() {
        String view = controller.showInternalServerErrorPage();
        assertEquals(ErrorController.VIEW_INTERNAL_SERVER_ERROR, view);
    }
}
