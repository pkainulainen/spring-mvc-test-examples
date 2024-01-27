package net.petrikainulainen.springmvctest.junit5.todoitem;

import net.petrikainulainen.springmvctest.junit5.IntegrationTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@JooqTest
@IntegrationTest
@Sql({
        "/db/clear-database.sql",
        "/db/init-todo-items.sql"
})
@DisplayName("Find todo item by using id as search criteria")
public class FindByIdTest {

    private final TodoItemRepository repository;

    @Autowired
    FindByIdTest(DSLContext jooq) {
        this.repository = new TodoItemRepository(jooq);
    }

    @Nested
    @DisplayName("When the todo item isn't found from the database")
    class WhenTodoItemIsNotFoundFromDatabase {

        @Test
        @DisplayName("Should return an empty optional")
        void shouldReturnEmptyOptional() {
            var todoItem = repository.findById(TodoItems.UNKNOWN_ID);
            assertThat(todoItem).isEmpty();
        }
    }

    @Nested
    @DisplayName("When the todo item is found from the database")
    class WhenTodoItemIsFoundFromDatabase {

        @Test
        @DisplayName("Should return an optional that contains the found todo item")
        void shouldReturnOptionalThatContainsFoundTodoItem() {
            var todoItem = repository.findById(TodoItems.WriteSampleCode.ID);
            assertThat(todoItem).isNotEmpty();
        }

        @Test
        @DisplayName("Should the information of the correct todo item")
        void shouldReturnInformationOfCorrectTodoItem() {
            var todoItem = repository.findById(TodoItems.WriteSampleCode.ID).get();
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(todoItem.getId())
                        .as("id")
                        .isEqualTo(TodoItems.WriteSampleCode.ID);
                softAssertions.assertThat(todoItem.getDescription())
                        .as("description")
                        .isEqualTo(TodoItems.WriteSampleCode.DESCRIPTION);
                softAssertions.assertThat(todoItem.getTitle())
                        .as("title")
                        .isEqualTo(TodoItems.WriteSampleCode.TITLE);
                softAssertions.assertThat(todoItem.getStatus())
                        .as("status")
                        .isEqualTo(TodoItems.WriteSampleCode.STATUS);
            });
        }
    }
}
