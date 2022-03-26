package net.petrikainulainen.springmvctest.junit5.todo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Contains the information of a new todo item that's
 * saved to a database.
 */
public class CreateTodoItemFormDTO {

    @Size(max = 1000)
    private String description;

    @NotBlank
    @Size(max = 100)
    private String title;

    public CreateTodoItemFormDTO() {}

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
