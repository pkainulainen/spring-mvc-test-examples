package net.petrikainulainen.spring.testmvc.todo.dto;

import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Petri Kainulainen
 */
public class TodoDTO {

    private Long id;

    @Length(max = Todo.MAX_LENGTH_DESCRIPTION)
    private String description;

    @NotEmpty
    @Length(max = Todo.MAX_LENGTH_TITLE)
    private String title;

    public TodoDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
