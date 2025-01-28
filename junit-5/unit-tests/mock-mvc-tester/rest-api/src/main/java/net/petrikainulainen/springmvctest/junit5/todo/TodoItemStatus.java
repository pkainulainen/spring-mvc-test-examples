package net.petrikainulainen.springmvctest.junit5.todo;

/**
 * Contains the status of a single todo item. The legal statuses are:
 * <ul>
 *     <li>
 *         <code>OPEN</code> means that we haven't started working on
 *         the todo item.
 *     </li>
 *     <li>
 *         <code>IN_PROGRESS</code> means that we are currently working
 *         on the todo item.
 *     </li>
 *     <li>
 *         <code>DONE</code> means that we have finished the todo item.
 *     </li>
 * </ul>
 */
public enum TodoItemStatus {
    OPEN,
    IN_PROGRESS,
    DONE
}
