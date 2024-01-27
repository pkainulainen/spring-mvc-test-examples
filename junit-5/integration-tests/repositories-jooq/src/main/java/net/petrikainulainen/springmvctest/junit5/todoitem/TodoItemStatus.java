package net.petrikainulainen.springmvctest.junit5.todoitem;

/**
 * Identifies the status of a todo item.
 */
public enum TodoItemStatus {
    /**
     * The todo item is done.
     */
    DONE,
    /**
     * The todo item is under work.
     */
    IN_PROGRESS,
    /**
     * No one has started working on the todo item.
     */
    OPEN
}
