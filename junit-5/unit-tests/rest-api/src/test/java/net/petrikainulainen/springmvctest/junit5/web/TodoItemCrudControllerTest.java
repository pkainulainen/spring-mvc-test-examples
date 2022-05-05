package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.TagDTO;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemCrudService;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemDTO;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemNotFoundException;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemStatus;
import net.petrikainulainen.springmvctest.junit5.todo.TodoListItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static net.petrikainulainen.springmvctest.junit5.web.WebTestConfig.objectMapperHttpMessageConverter;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TodoItemCrudControllerTest {

    private TodoItemRequestBuilder requestBuilder;
    private TodoItemCrudService service;

    @BeforeEach
    void configureSystemUnderTest() {
        service = mock(TodoItemCrudService.class);

        TodoItemCrudController testedController = new TodoItemCrudController(service);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(testedController)
                .setControllerAdvice(new TodoItemErrorHandler())
                .setMessageConverters(objectMapperHttpMessageConverter())
                .build();
        requestBuilder = new TodoItemRequestBuilder(mockMvc);
    }

    @Nested
    @DisplayName("Find all todo items")
    class FindAll {

        @Test
        @DisplayName("Should return the HTTP status code OK (200)")
        void shouldReturnHttpStatusCodeOk() throws Exception {
            requestBuilder.findAll()
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return the found todo items as Json")
        void shouldReturnFoundTodoItemAsJson() throws Exception {
            requestBuilder.findAll()
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Nested
        @DisplayName("When no todo item is found from the database")
        class WhenNoTodoItemsIsFound {

            @BeforeEach
            void returnEmptyList() {
                given(service.findAll()).willReturn(new ArrayList<>());
            }

            @Test
            @DisplayName("Should return zero todo items")
            void shouldReturnZeroTodoItems() throws Exception {
                requestBuilder.findAll()
                        .andExpect(jsonPath("$", hasSize(0)));
            }
        }

        @Nested
        @DisplayName("When two todo items are found from the database")
        class WhenTwoTodoItemsAreFound {

            private static final Long FIRST_TODO_ITEM_ID = 1L;
            private static final TodoItemStatus FIRST_TODO_ITEM_STATUS = TodoItemStatus.DONE;
            private static final String FIRST_TODO_ITEM_TITLE = "Write example application";

            private static final Long SECOND_TODO_ITEM_ID = 2L;
            private static final TodoItemStatus SECOND_TODO_ITEM_STATUS = TodoItemStatus.IN_PROGRESS;
            private static final String SECOND_TODO_ITEM_TITLE = "Write blog post";

            @BeforeEach
            void returnTwoTodoItems() {
                TodoListItemDTO first = new TodoListItemDTO();
                first.setId(FIRST_TODO_ITEM_ID);
                first.setStatus(FIRST_TODO_ITEM_STATUS);
                first.setTitle(FIRST_TODO_ITEM_TITLE);

                TodoListItemDTO second = new TodoListItemDTO();
                second.setId(SECOND_TODO_ITEM_ID);
                second.setStatus(SECOND_TODO_ITEM_STATUS);
                second.setTitle(SECOND_TODO_ITEM_TITLE);

                given(service.findAll()).willReturn(Arrays.asList(first, second));
            }

            @Test
            @DisplayName("Should return two todo items")
            void shouldReturnTwoTodoItems() throws Exception {
                requestBuilder.findAll()
                        .andExpect(jsonPath("$", hasSize(2)));
            }

            @Test
            @DisplayName("Should return the information of the first todo item")
            void shouldReturnInformationOfFirstTodoItem() throws Exception {
                requestBuilder.findAll()
                        .andExpect(jsonPath("$[0].id", equalTo(FIRST_TODO_ITEM_ID.intValue())))
                        .andExpect(jsonPath("$[0].status", equalTo(FIRST_TODO_ITEM_STATUS.name())))
                        .andExpect(jsonPath("$[0].title", equalTo(FIRST_TODO_ITEM_TITLE)));
            }

            @Test
            @DisplayName("Should return the information of the second todo item")
            void shouldReturnInformationOfSecondTodoItem() throws Exception {
                requestBuilder.findAll()
                        .andExpect(jsonPath("$[1].id", equalTo(SECOND_TODO_ITEM_ID.intValue())))
                        .andExpect(jsonPath("$[1].status", equalTo(SECOND_TODO_ITEM_STATUS.name())))
                        .andExpect(jsonPath("$[1].title", equalTo(SECOND_TODO_ITEM_TITLE)));
            }
        }
    }

    @Nested
    @DisplayName("Find todo item by using its id as search criteria")
    class FindById {

        private static final Long ID = 1L;

        @Nested
        @DisplayName("When the requested todo item isn't found")
        class WhenRequestedTodoItemIsNotFound {

            @BeforeEach
            void throwException() {
                given(service.findById(ID)).willThrow(new TodoItemNotFoundException(""));
            }

            @Test
            @DisplayName("Should return the HTTP status code not found (404)")
            void shouldReturnHttpStatusCodeNotFound() throws Exception {
                requestBuilder.findById(ID)
                        .andExpect(status().isNotFound());
            }

            @Test
            @DisplayName("Should return an HTTP response which has an empty response body")
            void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                requestBuilder.findById(ID)
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested todo item is found")
        class WhenRequestedTodoItemIsFound {

            private static final String DESCRIPTION = "Remember to use JUnit 5";
            private static final Long TAG_ID = 9L;
            private static final String TAG_NAME  = "Code";
            private static final String TITLE = "Write example application";
            private static final TodoItemStatus STATUS = TodoItemStatus.IN_PROGRESS;

            @BeforeEach
            void returnFoundTodoItem() {
                TodoItemDTO found = new TodoItemDTO();
                found.setId(ID);
                found.setDescription(DESCRIPTION);
                found.setStatus(STATUS);
                found.setTitle(TITLE);

                TagDTO tag = new TagDTO();
                tag.setId(TAG_ID);
                tag.setName(TAG_NAME);
                found.setTags(Arrays.asList(tag));

                given(service.findById(ID)).willReturn(found);
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                requestBuilder.findById(ID)
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found todo item as Json")
            void shouldReturnInformationOfFoundTodoItemAsJson() throws Exception {
                requestBuilder.findById(ID)
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found todo item")
            void shouldReturnInformationOfFoundTodoItem() throws Exception {
                requestBuilder.findById(ID)
                        .andExpect(jsonPath("$.id", equalTo(ID.intValue())))
                        .andExpect(jsonPath("$.description", equalTo(DESCRIPTION)))
                        .andExpect(jsonPath("$.status", equalTo(STATUS.name())))
                        .andExpect(jsonPath("$.title", equalTo(TITLE)));
            }

            @Test
            @DisplayName("Should return a todo item that has one tag")
            void shouldReturnTodoItemThatHasOneTag() throws Exception {
                requestBuilder.findById(ID)
                        .andExpect(jsonPath("$.tags", hasSize(1)));
            }

            @Test
            @DisplayName("Should return the information of the found tag")
            void shouldReturnInformationOfFoundTag() throws Exception {
                requestBuilder.findById(ID)
                        .andExpect(jsonPath("$.tags[0].id", equalTo(TAG_ID.intValue())))
                        .andExpect(jsonPath("$.tags[0].name", equalTo(TAG_NAME)));
            }
        }
    }
}
