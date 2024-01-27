package net.petrikainulainen.springmvctest.junit5.todoitem;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Contains the information of one todo item.
 */
public class TodoItemDTO {

    private Long id;
    private String description;
    private String title;
    private TodoItemStatus status;

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public TodoItemStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(TodoItemStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", this.id)
                .append("description", this.description)
                .append("title", this.title)
                .append("status", this.status)
                .toString();
    }
}
