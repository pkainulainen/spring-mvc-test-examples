package net.petrikainulainen.spring.testmvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Petri Kainulainen
 */
@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    protected static final String VIEW_HOME_PAGE = "index";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showHomePage() {
        LOGGER.debug("Rendering home page.");
        return VIEW_HOME_PAGE;
    }
}
