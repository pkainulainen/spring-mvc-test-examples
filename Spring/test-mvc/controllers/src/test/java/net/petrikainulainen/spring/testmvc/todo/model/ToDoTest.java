package net.petrikainulainen.spring.testmvc.todo.model;

import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Petri Kainulainen
 */
public class ToDoTest {

    private String TITLE = "title";
    private String DESCRIPTION = "description";

    @Test
    public void buildWithMandatoryInformation() {
        ToDo built = ToDo.getBuilder(TITLE).build();

        assertNull(built.getId());
        assertNull(built.getCreationTime());
        assertNull(built.getDescription());
        assertNull(built.getModificationTime());
        assertEquals(TITLE, built.getTitle());
        assertEquals(0L, built.getVersion());
    }

    @Test
    public void buildWithAllInformation() {
        ToDo built = ToDo.getBuilder(TITLE)
                .description(DESCRIPTION)
                .build();

        assertNull(built.getId());
        assertNull(built.getCreationTime());
        assertEquals(DESCRIPTION, built.getDescription());
        assertNull(built.getModificationTime());
        assertEquals(TITLE, built.getTitle());
        assertEquals(0L, built.getVersion());
    }

    @Test
    public void prePersist() {
        ToDo todo = new ToDo();
        todo.prePersist();

        assertNull(todo.getId());
        assertNotNull(todo.getCreationTime());
        assertNull(todo.getDescription());
        assertNotNull(todo.getModificationTime());
        assertNull(todo.getTitle());
        assertEquals(0L, todo.getVersion());
        assertEquals(todo.getCreationTime(), todo.getModificationTime());
    }

    @Test
    public void preUpdate() {
        ToDo todo = new ToDo();
        todo.prePersist();

        pause(1000);

        todo.preUpdate();

        assertNull(todo.getId());
        assertNotNull(todo.getCreationTime());
        assertNull(todo.getDescription());
        assertNotNull(todo.getModificationTime());
        assertNull(todo.getTitle());
        assertEquals(0L, todo.getVersion());
        assertTrue(todo.getModificationTime().isAfter(todo.getCreationTime()));
    }

    private void pause(long timeInMillis) {
        try {
            Thread.currentThread().sleep(timeInMillis);
        }
        catch (InterruptedException e) {
            //Do Nothing
        }
    }
}
