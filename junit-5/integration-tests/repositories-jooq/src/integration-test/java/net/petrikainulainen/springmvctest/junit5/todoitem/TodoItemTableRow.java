package net.petrikainulainen.springmvctest.junit5.todoitem;

/**
 *  Specifies the row indexes of each todo item that's found from
 *  the <code>todo_item</code> database table.
 */
enum TodoItemTableRow {

    NEW_TODO_ITEM(0),
    WRITE_BLOG_POST(1),
    WRITE_SAMPLE_CODE(0);

    private final int index;

    private TodoItemTableRow(int index) {
        this.index = index;
    }

    int getIndex() {
        return index;
    }
}
