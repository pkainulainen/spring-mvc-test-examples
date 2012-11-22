package net.petrikainulainen.spring.testmvc.todo.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import net.petrikainulainen.spring.testmvc.SpringTestMvcRule;
import net.petrikainulainen.spring.testmvc.ApplicationContextSetup;
import net.petrikainulainen.spring.testmvc.config.IntegrationTestApplicationContext;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.server.MockMvc;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * This test uses the xml based application context configuration.
 * @author Petri Kainulainen
 */
@ApplicationContextSetup(configurationFile = "classpath:exampleApplicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestApplicationContext.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("toDoData.xml")
public class ITXmlTodoControllerTest {

    @Resource
    private DataSource datasource;

    @Rule
    public SpringTestMvcRule rule = new SpringTestMvcRule(this);

    private MockMvc mockMvc;


}
