package net.petrikainulainen.springmvctest.junit5.todoitem;

import net.petrikainulainen.springmvctest.junit5.IdColumnReset;
import net.petrikainulainen.springmvctest.junit5.IntegrationTest;
import org.assertj.db.type.Table;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static net.petrikainulainen.springmvctest.junit5.jooq.Tables.TODO_ITEM;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.assertj.db.api.Assertions.assertThat;

@JooqTest
//By default, jOOQ tests are transactional and roll back at the end of each test.
//We must disable transaction management for our tests so that we can write assertions
//for the data found from the database.
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@IntegrationTest
@Sql({
        "/db/clear-database.sql"
})
@DisplayName("Create a new todo item")
class CreateTodoItemTest {

    private final IdColumnReset idColumnReset;
    private final TodoItemRepository repository;

    private CreateTodoItem input;
    private Table todoItemTable;

    @Autowired
    CreateTodoItemTest(DataSource dataSource,
                       DSLContext jooq) {
        this.idColumnReset = new IdColumnReset(new NamedParameterJdbcTemplate(dataSource));
        this.repository = new TodoItemRepository(jooq);
        this.todoItemTable = new Table(dataSource, TODO_ITEM.getName());
    }

    @BeforeEach
    void configureSystemUnderTest() {
        idColumnReset.resetIdColumns(TODO_ITEM.getName());
        input = createInput();
    }

    private CreateTodoItem createInput() {
        var input = new CreateTodoItem();
        input.setDescription(TodoItems.WriteSampleCode.DESCRIPTION);
        input.setTitle(TodoItems.WriteSampleCode.TITLE);
        return input;
    }

    @Test
    @DisplayName("Should insert a new todo item into the database")
    void shouldInsertNewTodoItemIntoDatabase() {
        repository.create(input);
        assertThat(todoItemTable).hasNumberOfRows(1);
    }

    @Test
    @DisplayName("Should use the next free id when a new todo item is inserted into the database")
    void shouldUseNextFreeIdWhenNewTodoItemIsInsertedIntoDatabase() {
        repository.create(input);
        assertThat(todoItemTable)
                .row(TodoItemTableRow.NEW_TODO_ITEM.getIndex())
                .value(TODO_ITEM.ID.getName())
                .isEqualTo(TodoItems.NEXT_FREE_ID);
    }

    @Test
    @DisplayName("Should create a todo item which has the correct description")
    void shouldCreateTodoItemWhichHasCorrectDescription() {
        repository.create(input);
        assertThat(todoItemTable)
                .row(TodoItemTableRow.NEW_TODO_ITEM.getIndex())
                .value(TODO_ITEM.DESCRIPTION.getName())
                .isEqualTo(TodoItems.WriteSampleCode.DESCRIPTION);
    }

    @Test
    @DisplayName("Should create a todo item which has the correct title")
    void shouldCreateTodoItemWhichHasCorrectTitle() {
        repository.create(input);
        assertThat(todoItemTable)
                .row(TodoItemTableRow.NEW_TODO_ITEM.getIndex())
                .value(TODO_ITEM.TITLE.getName())
                .isEqualTo(TodoItems.WriteSampleCode.TITLE);
    }

    @Test
    @DisplayName("Should create an open todo item")
    void shouldCreateOpenTodoItem() {
        repository.create(input);
        assertThat(todoItemTable)
                .row(TodoItemTableRow.NEW_TODO_ITEM.getIndex())
                .value(TODO_ITEM.STATUS.getName())
                .isEqualTo(TodoItemStatus.OPEN.name());
    }

    @Test
    @DisplayName("Should return the information of the created todo item")
    void shouldReturnInformationOfCreatedTodoItem() {
        var created = repository.create(input);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(created.getId())
                    .as("id")
                    .isEqualTo(TodoItems.NEXT_FREE_ID);
            softAssertions.assertThat(created.getDescription())
                    .as("description")
                    .isEqualTo(TodoItems.WriteSampleCode.DESCRIPTION);
            softAssertions.assertThat(created.getTitle())
                    .as("title")
                    .isEqualTo(TodoItems.WriteSampleCode.TITLE);
            softAssertions.assertThat(created.getStatus())
                    .as("status")
                    .isEqualTo(TodoItemStatus.OPEN);
        });
    }
}
