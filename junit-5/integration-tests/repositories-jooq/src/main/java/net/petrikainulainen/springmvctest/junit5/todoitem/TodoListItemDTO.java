package net.petrikainulainen.springmvctest.junit5.todoitem;

/**
 * Contains the minimum information of one todo item which
 * is used when we want to get a list of todo items.
 */
public class TodoListItemDTO {

    private Long id;
    private String title;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
