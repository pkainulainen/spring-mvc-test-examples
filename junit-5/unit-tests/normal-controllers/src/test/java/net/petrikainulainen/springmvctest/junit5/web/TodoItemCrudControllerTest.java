package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static net.petrikainulainen.springmvctest.junit5.web.WebTestConfig.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TodoItemCrudControllerTest {

    private TodoItemRequestBuilder requestBuilder;
    private TodoItemCrudService service;

    @BeforeEach
    void configureSystemUnderTest() {
        service = mock(TodoItemCrudService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new TodoItemCrudController(service))
                .setHandlerExceptionResolvers(exceptionResolver())
                .setLocaleResolver(fixedLocaleResolver())
                .setViewResolvers(jspViewResolver())
                .build();
        requestBuilder = new TodoItemRequestBuilder(mockMvc);
    }

    @Nested
    @DisplayName("Render the HTML view that displays the information of all todo items")
    class FindAll {

        @Test
        @DisplayName("Should return the HTTP status code 200")
        void shouldReturnHttpStatusCodeOk() throws Exception {
            requestBuilder.findAll().andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should render the todo item list view")
        void shouldRenderTodoItemListView() throws Exception {
            requestBuilder.findAll().andExpect(view().name("todo-item/list"));
        }

        @Nested
        @DisplayName("When no todo items are found from the database")
        class WhenNoTodoItemsAreFoundFromDatabase {

            @BeforeEach
            void serviceReturnsEmptyList() {
                given(service.findAll()).willReturn(new ArrayList<>());
            }

            @Test
            @DisplayName("Should display zero todo items")
            void shouldDisplayZeroTodoItems() throws Exception {
                requestBuilder.findAll().andExpect(model().attribute("todoItems", hasSize(0)));
            }
        }

        @Nested
        @DisplayName("When two todo items are found from the database")
        class WhenTwoTodoItemsAreFoundFromDatabase {

            private final Long TODO_ITEM_ONE_ID = 1L;
            private final String TODO_ITEM_ONE_TITLE = "first todo item";
            private final Long TODO_ITEM_TWO_ID = 2L;
            private final String TODO_ITEM_TWO_TITLE = "second todo item";

            private final TodoItemStatus STATUS_OPEN = TodoItemStatus.OPEN;

            @BeforeEach
            void serviceReturnsTwoTodoItems() {
                TodoListItemDTO first = new TodoListItemDTO();
                first.setId(TODO_ITEM_ONE_ID);
                first.setTitle(TODO_ITEM_ONE_TITLE);
                first.setStatus(STATUS_OPEN);

                TodoListItemDTO second = new TodoListItemDTO();
                second.setId(TODO_ITEM_TWO_ID);
                second.setTitle(TODO_ITEM_TWO_TITLE);
                second.setStatus(STATUS_OPEN);

                given(service.findAll()).willReturn(Arrays.asList(first, second));
            }

            @Test
            @DisplayName("Should display two todo items")
            void shouldDisplayTwoTodoItems() throws Exception {
                requestBuilder.findAll().andExpect(model().attribute("todoItems", hasSize(2)));
            }

            /**
             * These two tests ensure that the list view displays the information
             * of the found todo items but they don't guarantee that todo items
             * are displayed in the correct order
             */
            @Test
            @DisplayName("Should display the information of the first todo item")
            void shouldDisplayInformationOfFirstTodoItem() throws Exception {
                requestBuilder.findAll()
                        .andExpect(
                                model().attribute("todoItems",
                                hasItem(allOf(
                                        hasProperty("id", equalTo(TODO_ITEM_ONE_ID)),
                                        hasProperty("title", equalTo(TODO_ITEM_ONE_TITLE)),
                                        hasProperty("status", equalTo(STATUS_OPEN))
                                )))
                        );
            }

            @Test
            @DisplayName("Should display the information of the second todo item")
            void shouldDisplayInformationOfSecondTodoItem() throws Exception {
                requestBuilder.findAll()
                        .andExpect(
                                model().attribute("todoItems",
                                        hasItem(allOf(
                                                hasProperty("id", equalTo(TODO_ITEM_TWO_ID)),
                                                hasProperty("title", equalTo(TODO_ITEM_TWO_TITLE)),
                                                hasProperty("status", equalTo(STATUS_OPEN))
                                        )))
                        );
            }

            /**
             * This test ensures that the list view displays the information
             * of the found todo items in the correct order.
             */
            @Test
            @DisplayName("Should display the information of the first and second todo item in the correct order")
            void shouldDisplayFirstAndSecondTodoItemInCorrectOrder() throws Exception {
                requestBuilder.findAll()
                        .andExpect(
                                model().attribute("todoItems",
                                        contains(
                                                allOf(
                                                        hasProperty("id", equalTo(TODO_ITEM_ONE_ID)),
                                                        hasProperty("title", equalTo(TODO_ITEM_ONE_TITLE)),
                                                        hasProperty("status", equalTo(STATUS_OPEN))
                                                ),
                                                allOf(
                                                        hasProperty("id", equalTo(TODO_ITEM_TWO_ID)),
                                                        hasProperty("title", equalTo(TODO_ITEM_TWO_TITLE)),
                                                        hasProperty("status", equalTo(STATUS_OPEN))
                                                )
                                        ))
                        );
            }
        }
    }

    @Nested
    @DisplayName("Render the HTML view that displays the information of the requested todo item")
    class FindById {

        private final Long TODO_ITEM_ID = 99L;

        @Nested
        @DisplayName("When the requested todo item isn't found from the database")
        class WhenRequestedTodoItemIsNotFound {

            @BeforeEach
            void serviceThrowsNotFoundException() {
                given(service.findById(TODO_ITEM_ID)).willThrow(new TodoItemNotFoundException(""));
            }

            @Test
            @DisplayName("Should return the HTTP status code 404")
            void shouldReturnHttpStatusCodeNotFound() throws Exception {
                requestBuilder.findById(TODO_ITEM_ID).andExpect(status().isNotFound());
            }

            @Test
            @DisplayName("Should render the 404 view")
            void shouldRender404View() throws Exception {
                requestBuilder.findById(TODO_ITEM_ID).andExpect(view().name("error/404"));
            }
        }

        @Nested
        @DisplayName("When the requested todo item is found from the database")
        class WhenRequestedTodoItemIsFound {

            private final String TITLE = "Write example project";
            private final String DESCRIPTION = "Use JUnit 5";
            private final TodoItemStatus STATUS_OPEN = TodoItemStatus.OPEN;

            private final Long TAG_ID = 44L;
            private final String TAG_NAME = "tag";

            @BeforeEach
            void serviceReturnsOpenTodoItemWithOneTag() {
                TodoItemDTO found = new TodoItemDTO();
                found.setId(TODO_ITEM_ID);
                found.setTitle(TITLE);
                found.setDescription(DESCRIPTION);
                found.setStatus(STATUS_OPEN);

                TagDTO tag = new TagDTO();
                tag.setId(TAG_ID);
                tag.setName(TAG_NAME);
                found.setTags(Collections.singletonList(tag));

                given(service.findById(TODO_ITEM_ID)).willReturn(found);
            }

            @Test
            @DisplayName("Should return the HTTP status code 200")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                requestBuilder.findById(TODO_ITEM_ID).andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should render the view todo item view")
            void shouldRenderViewTodoItemView() throws Exception {
                requestBuilder.findById(TODO_ITEM_ID).andExpect(view().name("todo-item/view"));
            }

            @Test
            @DisplayName("Should display the information of the correct todo item")
            void shouldDisplayInformationOfCorrectTodoItem() throws Exception {
                requestBuilder.findById(TODO_ITEM_ID)
                        .andExpect(model().attribute(
                                "todoItem",
                                hasProperty("id", equalTo(TODO_ITEM_ID))
                        ));
            }

            @Test
            @DisplayName("Should display the correct title and description")
            void shouldDisplayCorrectTitleAndDescription() throws Exception {
                requestBuilder.findById(TODO_ITEM_ID)
                        .andExpect(model().attribute(
                                "todoItem",
                                allOf(
                                        hasProperty("title", equalTo(TITLE)),
                                        hasProperty("description", equalTo(DESCRIPTION))
                                )
                        ));
            }

            @Test
            @DisplayName("Should display an open todo item")
            void shouldDisplayOpenTodoItem() throws Exception {
                requestBuilder.findById(TODO_ITEM_ID)
                        .andExpect(model().attribute(
                                "todoItem",
                                hasProperty("status", equalTo(STATUS_OPEN))
                        ));
            }

            @Test
            @DisplayName("Should display a todo item that has one tag")
            void shouldDisplayTodoItemThatHasOneTag() throws Exception {
                requestBuilder.findById(TODO_ITEM_ID)
                        .andExpect(model().attribute(
                                "todoItem",
                                hasProperty("tags", hasSize(1))
                        ));
            }

            @Test
            @DisplayName("Should display the information of the found tag")
            void shouldDisplayInformationOfFoundTag() throws Exception {
                requestBuilder.findById(TODO_ITEM_ID)
                        .andExpect(model().attribute(
                                "todoItem",
                                hasProperty("tags", hasItem(
                                        allOf(
                                                hasProperty("id", equalTo(TAG_ID)),
                                                hasProperty("name", equalTo(TAG_NAME))
                                        )
                                ))
                        ));
            }
        }
    }
}
