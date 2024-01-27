package net.petrikainulainen.springmvctest.junit5.todoitem;

import net.petrikainulainen.springmvctest.junit5.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.assertj.db.api.SoftAssertions;
import org.assertj.db.type.Table;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
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
        "/db/clear-database.sql",
        "/db/init-todo-items.sql"
})
@DisplayName("Updates the information of the specified todo item")
class UpdateTodoItemTest {

    private final TodoItemRepository repository;

    private UpdateTodoItem input;
    private Table todoItemTable;

    @Autowired
    UpdateTodoItemTest(DataSource dataSource, DSLContext jooq) {
        this.repository = new TodoItemRepository(jooq);
        this.todoItemTable = new Table(dataSource, TODO_ITEM.getName());
    }

    @BeforeEach
    void createInput() {
        input = new UpdateTodoItem();
        input.setDescription(TodoItems.WriteSampleCode.UPDATED_DESCRIPTION);
        input.setTitle(TodoItems.WriteSampleCode.UPDATED_TITLE);
    }

    @Nested
    @DisplayName("When the updated todo item isn't found from the database")
    class WhenUpdatedTodoItemIsNotFoundFromDatabase {

        @BeforeEach
        void useUnknownId() {
            input.setId(TodoItems.UNKNOWN_ID);
        }

        @Test
        @DisplayName("Shouldn't insert new todo items into the database")
        void shouldNotInsertNewTodoItemsIntoDatabase() {
            repository.update(input);
            assertThat(todoItemTable).hasNumberOfRows(TodoItems.TODO_ITEM_COUNT);
        }

        @Test
        @DisplayName("Shouldn't make any changes to the information of the todo item: write sample code")
        void shouldNotMakeAnyChangesToInformationOfTodoItemWriteSampleCode() {
            repository.update(input);

            var softAssertions = new SoftAssertions();
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_SAMPLE_CODE.getIndex())
                    .value(TODO_ITEM.ID.getName())
                    .as(TODO_ITEM.ID.getName())
                    .isEqualTo(TodoItems.WriteSampleCode.ID);
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_SAMPLE_CODE.getIndex())
                    .value(TODO_ITEM.DESCRIPTION.getName())
                    .as(TODO_ITEM.DESCRIPTION.getName())
                    .isEqualTo(TodoItems.WriteSampleCode.DESCRIPTION);
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_SAMPLE_CODE.getIndex())
                    .value(TODO_ITEM.TITLE.getName())
                    .as(TODO_ITEM.TITLE.getName())
                    .isEqualTo(TodoItems.WriteSampleCode.TITLE);
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_SAMPLE_CODE.getIndex())
                    .value(TODO_ITEM.STATUS.getName())
                    .as(TODO_ITEM.STATUS.getName())
                    .isEqualTo(TodoItems.WriteSampleCode.STATUS.name());
            softAssertions.assertAll();
        }

        @Test
        @DisplayName("Shouldn't make any changes to the information of the todo item: write blog post")
        void shouldNotMakeAnyChangesToInformationOfTodoItemWriteBlogPost() {
            repository.update(input);

            var softAssertions = new SoftAssertions();
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_BLOG_POST.getIndex())
                    .value(TODO_ITEM.ID.getName())
                    .as(TODO_ITEM.ID.getName())
                    .isEqualTo(TodoItems.WriteBlogPost.ID);
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_BLOG_POST.getIndex())
                    .value(TODO_ITEM.DESCRIPTION.getName())
                    .as(TODO_ITEM.DESCRIPTION.getName())
                    .isEqualTo(TodoItems.WriteBlogPost.DESCRIPTION);
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_BLOG_POST.getIndex())
                    .value(TODO_ITEM.TITLE.getName())
                    .as(TODO_ITEM.TITLE.getName())
                    .isEqualTo(TodoItems.WriteBlogPost.TITLE);
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_BLOG_POST.getIndex())
                    .value(TODO_ITEM.STATUS.getName())
                    .as(TODO_ITEM.STATUS.getName())
                    .isEqualTo(TodoItems.WriteBlogPost.STATUS.name());
            softAssertions.assertAll();
        }

        @Test
        @DisplayName("Should return an empty optional")
        void shouldReturnEmptyOptional() {
            var updatedTodoItem = repository.update(input);
            Assertions.assertThat(updatedTodoItem).isEmpty();
        }
    }

    @Nested
    @DisplayName("When the updated todo item is found from the database")
    class WhenUpdatedTodoItemIsFoundFromDatabase {

        @BeforeEach
        void setId() {
            input.setId(TodoItems.WriteSampleCode.ID);
        }

        @Test
        @DisplayName("Shouldn't insert new todo items into the database")
        void shouldNotInsertNewTodoItemsIntoDatabase() {
            repository.update(input);
            assertThat(todoItemTable).hasNumberOfRows(TodoItems.TODO_ITEM_COUNT);
        }

        @Test
        @DisplayName("Shouldn't change the id of the updated todo item")
        void shouldNotChangeIdOfUpdatedTodoItem() {
            repository.update(input);
            assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_SAMPLE_CODE.getIndex())
                    .value(TODO_ITEM.ID.getName())
                    .isEqualTo(TodoItems.WriteSampleCode.ID);
        }

        @Test
        @DisplayName("Should update the description of the updated todo item")
        void shouldUpdateDescriptionOfUpdatedTodoItem() {
            repository.update(input);
            assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_SAMPLE_CODE.getIndex())
                    .value(TODO_ITEM.DESCRIPTION.getName())
                    .isEqualTo(TodoItems.WriteSampleCode.UPDATED_DESCRIPTION);
        }

        @Test
        @DisplayName("Should update the title of the updated todo item")
        void shouldUpdateTitleOfUpdatedTodoItem() {
            repository.update(input);
            assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_SAMPLE_CODE.getIndex())
                    .value(TODO_ITEM.TITLE.getName())
                    .isEqualTo(TodoItems.WriteSampleCode.UPDATED_TITLE);
        }

        @Test
        @DisplayName("Shouldn't change the status of the updated todo item")
        void shouldNotChangeStatusOfUpdatedTodoItem() {
            repository.update(input);
            assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_SAMPLE_CODE.getIndex())
                    .value(TODO_ITEM.STATUS.getName())
                    .isEqualTo(TodoItems.WriteSampleCode.STATUS.name());
        }

        @Test
        @DisplayName("Shouldn't make any changes to the information of the todo item: write blog post")
        void shouldNotMakeAnyChangesToInformationOfTodoItemWriteBlogPost() {
            repository.update(input);

            var softAssertions = new SoftAssertions();
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_BLOG_POST.getIndex())
                    .value(TODO_ITEM.ID.getName())
                    .as(TODO_ITEM.ID.getName())
                    .isEqualTo(TodoItems.WriteBlogPost.ID);
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_BLOG_POST.getIndex())
                    .value(TODO_ITEM.DESCRIPTION.getName())
                    .as(TODO_ITEM.DESCRIPTION.getName())
                    .isEqualTo(TodoItems.WriteBlogPost.DESCRIPTION);
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_BLOG_POST.getIndex())
                    .value(TODO_ITEM.TITLE.getName())
                    .as(TODO_ITEM.TITLE.getName())
                    .isEqualTo(TodoItems.WriteBlogPost.TITLE);
            softAssertions.assertThat(todoItemTable)
                    .row(TodoItemTableRow.WRITE_BLOG_POST.getIndex())
                    .value(TODO_ITEM.STATUS.getName())
                    .as(TODO_ITEM.STATUS.getName())
                    .isEqualTo(TodoItems.WriteBlogPost.STATUS.name());
            softAssertions.assertAll();
        }

        @Test
        @DisplayName("Should return an optional that contains the updated todo item")
        void shouldReturnOptionalThatContainsUpdatedTodoItem() {
            var updatedTodoItem = repository.update(input);
            Assertions.assertThat(updatedTodoItem).isNotEmpty();
        }

        @Test
        @DisplayName("Should return the new information of the updated todo item")
        void shouldReturnNewInformationOfUpdatedTodoItem() {
            var updatedTodoItem = repository.update(input).get();
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(updatedTodoItem.getId())
                        .as("id")
                        .isEqualTo(TodoItems.WriteSampleCode.ID);
                softAssertions.assertThat(updatedTodoItem.getDescription())
                        .as("description")
                        .isEqualTo(TodoItems.WriteSampleCode.UPDATED_DESCRIPTION);
                softAssertions.assertThat(updatedTodoItem.getTitle())
                        .as("title")
                        .isEqualTo(TodoItems.WriteSampleCode.UPDATED_TITLE);
                softAssertions.assertThat(updatedTodoItem.getStatus())
                        .as("status")
                        .isEqualTo(TodoItems.WriteSampleCode.STATUS);
            });
        }
    }
}
