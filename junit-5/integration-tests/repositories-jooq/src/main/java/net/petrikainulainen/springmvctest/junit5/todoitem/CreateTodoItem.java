package net.petrikainulainen.springmvctest.junit5.todoitem;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Contains the information of the created todo item.
 */
class CreateTodoItem {

    private String description;
    private String title;

    String getDescription() {
        return description;
    }

    String getTitle() {
        return title;
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
                .append("description", this.description)
                .append("title", this.title)
                .toString();
    }
}
