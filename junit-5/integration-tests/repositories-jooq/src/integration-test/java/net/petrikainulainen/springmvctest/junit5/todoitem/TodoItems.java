package net.petrikainulainen.springmvctest.junit5.todoitem;

/**
 * Contains the test data that's found from the <code>todo_item</code>
 * database table.
 */
public final class TodoItems {

    public static final Long NEXT_FREE_ID = 1L;
    public static final int TODO_ITEM_COUNT = 2;
    public static final Long UNKNOWN_ID = 99L;

    /**
     * Prevents instantiation.
     */
    private TodoItems() {}

    /**
     * Contains the information which should be found from the first row
     * of the <code>todo_item</code> database table.
     */
    public class WriteSampleCode {

        public static final Long ID = 1L;
        public static final String DESCRIPTION = "Write runnable Maven project";
        public static final String TITLE = "Write sample code";
        public static final String UPDATED_DESCRIPTION = "Use Maven";
        public static final String UPDATED_TITLE = "Write sample project";
        public static final TodoItemStatus STATUS = TodoItemStatus.DONE;
    }

    /**
     * Contains the information which should be found from the second row
     * of the <code>todo_item</code> database table.
     */
    public class WriteBlogPost {

        public static final Long ID = 2L;
        public static final String DESCRIPTION = "Publish the blog post on petrikainulainen.net";
        public static final String TITLE = "Write new blog post";
        public static final TodoItemStatus STATUS = TodoItemStatus.OPEN;
    }
}
