package net.petrikainulainen.springmvctest.junit5.todo;

import java.util.List;

/**
 * Contains the information of a single todo item.
 */
public class TodoItemDTO {

    private Long id;
    private String description;
    private List<TagDTO> tags;
    private String title;
    private TodoItemStatus status;

    public TodoItemDTO() {}

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public List<TagDTO> getTags() {
        return tags;
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

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(TodoItemStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TodoItemDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", title='" + title + '\'' +
                ", status=" + status +
                '}';
    }
}
