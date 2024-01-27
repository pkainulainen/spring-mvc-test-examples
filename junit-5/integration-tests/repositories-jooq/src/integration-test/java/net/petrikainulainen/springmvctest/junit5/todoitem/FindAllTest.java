package net.petrikainulainen.springmvctest.junit5.todoitem;

import net.petrikainulainen.springmvctest.junit5.IntegrationTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@JooqTest
@IntegrationTest
@DisplayName("Find all todo items from the database")
class FindAllTest {

    private final TodoItemRepository repository;

    @Autowired
    FindAllTest(DSLContext jooq) {
        this.repository = new TodoItemRepository(jooq);
    }

    @Nested
    @DisplayName("When no todo items is found from the database")
    @Sql({
            "/db/clear-database.sql"
    })
    class WhenNoTodoItemsIsFoundFromDatabase {

        @Test
        @DisplayName("Should return an empty list")
        void shouldReturnEmptyList() {
            var todoItems = repository.findAll();
            assertThat(todoItems).isEmpty();
        }
    }

    @Nested
    @DisplayName("When two todo items is found from the database")
    @Sql({
            "/db/clear-database.sql",
            "/db/init-todo-items.sql"
    })
    class WhenTwoTodoItemsIsFoundFromDatabase {

        @Test
        @DisplayName("Should return two todo items")
        void shouldReturnTwoTodoItems() {
            var todoItems = repository.findAll();
            assertThat(todoItems).hasSize(2);
        }

        @Test
        @DisplayName("Should return the correct information of the first todo item")
        void shouldReturnCorrectInformationOfFirstTodoItem() {
            var firstTodoItem = repository.findAll().get(0);
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(firstTodoItem.getId())
                        .as("id")
                        .isEqualTo(TodoItems.WriteSampleCode.ID);
                softAssertions.assertThat(firstTodoItem.getTitle())
                        .as("title")
                        .isEqualTo(TodoItems.WriteSampleCode.TITLE);
            });
        }

        @Test
        @DisplayName("Should return the correct information of the second todo item")
        void shouldReturnCorrectInformationOfSecondTodoItem() {
            var secondTodoItem = repository.findAll().get(1);
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(secondTodoItem.getId())
                        .as("id")
                        .isEqualTo(TodoItems.WriteBlogPost.ID);
                softAssertions.assertThat(secondTodoItem.getTitle())
                        .as("title")
                        .isEqualTo(TodoItems.WriteBlogPost.TITLE);
            });
        }
    }
}
