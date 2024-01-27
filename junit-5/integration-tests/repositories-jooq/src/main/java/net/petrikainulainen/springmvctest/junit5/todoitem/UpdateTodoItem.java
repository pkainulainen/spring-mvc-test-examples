package net.petrikainulainen.springmvctest.junit5.todoitem;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Contains the new information of the updated todo item.
 */
class UpdateTodoItem {

    private Long id;
    private String description;
    private String title;

    Long getId() {
        return id;
    }

    String getDescription() {
        return description;
    }

    String getTitle() {
        return title;
    }

    void setId(Long id) {
        this.id = id;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", this.id)
                .append("description", this.description)
                .append("title", this.title)
                .build();
    }
}
