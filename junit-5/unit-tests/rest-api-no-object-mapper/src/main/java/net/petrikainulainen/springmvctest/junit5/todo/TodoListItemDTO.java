package net.petrikainulainen.springmvctest.junit5.todo;

/**
 * Contains the information of a single todo item
 * that's shown on a list.
 */
public class TodoListItemDTO {

    private Long id;
    private String title;
    private TodoItemStatus status;

    public TodoListItemDTO() {}

    public Long getId() {
        return id;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(TodoItemStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TodoListItemDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                '}';
    }
}
