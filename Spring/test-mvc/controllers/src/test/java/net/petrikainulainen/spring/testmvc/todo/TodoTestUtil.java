package net.petrikainulainen.spring.testmvc.todo;

import net.petrikainulainen.spring.testmvc.todo.dto.ToDoDTO;
import net.petrikainulainen.spring.testmvc.todo.model.ToDo;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Petri Kainulainen
 */
public class ToDoTestUtil {

    public static final Long ID = 1L;
    public static final String DESCRIPTION = "description";
    public static final String DESCRIPTION_UPDATED = "updatedDescription";
    public static final String TITLE = "title";
    public static final String TITLE_UPDATED = "updatedTitle";

    private static final String CHARACTER = "a";

    public static ToDoDTO createDTO(Long id, String description, String title) {
        ToDoDTO dto = new ToDoDTO();

        dto.setId(id);
        dto.setDescription(description);
        dto.setTitle(title);

        return dto;
    }

    public static ToDo createModel(Long id, String description, String title) {
        ToDo model = ToDo.getBuilder(title)
                .description(description)
                .build();

        ReflectionTestUtils.setField(model, "id", id);

        return model;
    }

    public static String createRedirectViewPath(String path) {
        StringBuilder redirectViewPath = new StringBuilder();
        redirectViewPath.append("redirect:");
        redirectViewPath.append(path);
        return redirectViewPath.toString();
    }

    public static final String createStringWithLength(int length) {
        StringBuilder builder = new StringBuilder();

        for (int index = 0; index < length; index++) {
            builder.append(CHARACTER);
        }

        return builder.toString();
    }
}
