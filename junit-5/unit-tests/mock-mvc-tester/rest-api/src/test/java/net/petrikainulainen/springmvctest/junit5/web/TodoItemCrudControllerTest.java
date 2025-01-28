package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.CreateTodoItemDTO;
import net.petrikainulainen.springmvctest.junit5.todo.TagDTO;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemCrudService;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemDTO;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemNotFoundException;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemStatus;
import net.petrikainulainen.springmvctest.junit5.todo.TodoListItemDTO;
import net.petrikainulainen.springmvctest.junit5.web.TodoItemRequestBuilder.RequestBodyTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static net.petrikainulainen.springmvctest.junit5.web.TodoItemRequestBuilder.*;
import static net.petrikainulainen.springmvctest.junit5.web.WebTestConfig.objectMapperHttpMessageConverter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    @DisplayName("Create a new todo item")
    class Create {

        private static final int MAX_LENGTH_DESCRIPTION = 1000;
        private static final int MAX_LENGTH_TITLE = 100;

        private CreateTodoItemDTO input;

        @Nested
        @DisplayName("When the information of the created todo item isn't valid")
        class WhenInvalidInformationIsProvided {

            private static final String VALIDATION_ERROR_CODE_EMPTY_VALUE = "NotBlank";
            private static final String VALIDATION_ERROR_CODE_MISSING_VALUE = "NotBlank";
            private static final String VALIDATION_ERROR_CODE_TOO_LONG_VALUE = "Size";

            @Nested
            @DisplayName("When the field values are missing")
            class WhenFieldValuesAreMissing {

                private final Map<String, Object> NO_TEMPLATE_VARIABLES = Map.of();

                @Test
                @DisplayName("Should return the HTTP status code bad request (400)")
                void shouldReturnHttpStatusCodeBadRequest() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.EMPTY_TODO_ITEM, NO_TEMPLATE_VARIABLES))
                            .hasStatus(HttpStatus.BAD_REQUEST);
                }

                @Test
                @DisplayName("Should return validation errors as JSON")
                void shouldReturnValidationErrorsAsJson() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.EMPTY_TODO_ITEM, NO_TEMPLATE_VARIABLES))
                            .hasContentType(MediaType.APPLICATION_JSON);
                }

                @Test
                @DisplayName("Should return one validation error")
                void shouldReturnOneValidationError() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.EMPTY_TODO_ITEM, NO_TEMPLATE_VARIABLES))
                            .bodyJson()
                            .extractingPath("$.fieldErrors.length()")
                            .isEqualTo(1);
                }

                @Test
                @DisplayName("Should return a validation error about missing title")
                void shouldReturnValidationErrorAboutMissingTitle() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.EMPTY_TODO_ITEM, NO_TEMPLATE_VARIABLES))
                            .bodyJson()
                            .extractingPath("$.fieldErrors[?(@.field == 'title')].errorCode")
                            .asArray()
                            .containsExactly(VALIDATION_ERROR_CODE_MISSING_VALUE);
                }

                @Test
                @DisplayName("Shouldn't create a new todo item")
                void shouldNotCreateNewTodoItem() {
                    requestBuilder.create(RequestBodyTemplate.EMPTY_TODO_ITEM, NO_TEMPLATE_VARIABLES);

                    verify(service, never()).create(any());
                }
            }


            @Nested
            @DisplayName("When the field values are empty strings")
            class WhenFieldValuesAreEmptyStrings {

                private final Map<String, Object> TEMPLATE_VARIABLES = Map.of(
                        TEMPLATE_VARIABLE_DESCRIPTION, "",
                        TEMPLATE_VARIABLE_TITLE, ""
                );

                @Test
                @DisplayName("Should return the HTTP status code bad request (400)")
                void shouldReturnHttpStatusCodeBadRequest() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .hasStatus(HttpStatus.BAD_REQUEST);
                }

                @Test
                @DisplayName("Should return validation errors as JSON")
                void shouldReturnValidationErrorsAsJson() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .hasContentType(MediaType.APPLICATION_JSON);
                }

                @Test
                @DisplayName("Should return one validation error")
                void shouldReturnOneValidationError() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .bodyJson()
                            .extractingPath("$.fieldErrors.length()")
                            .isEqualTo(1);
                }

                @Test
                @DisplayName("Should return a validation error about empty title")
                void shouldReturnValidationErrorAboutEmptyTitle() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .bodyJson()
                            .extractingPath("$.fieldErrors[?(@.field == 'title')].errorCode")
                            .asArray()
                            .containsExactly(VALIDATION_ERROR_CODE_EMPTY_VALUE);
                }

                @Test
                @DisplayName("Shouldn't create a new todo item")
                void shouldNotCreateNewTodoItem() {
                    requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES);

                    verify(service, never()).create(any());
                }
            }

            @Nested
            @DisplayName("When the field values contain only whitespace characters")
            class WhenFieldValuesContainOnlyWhitespaceCharacters {

                private final Map<String, Object> TEMPLATE_VARIABLES = Map.of(
                        TEMPLATE_VARIABLE_DESCRIPTION, "      ",
                        TEMPLATE_VARIABLE_TITLE, "            "
                );

                @Test
                @DisplayName("Should return the HTTP status code bad request (400)")
                void shouldReturnHttpStatusCodeBadRequest() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .hasStatus(HttpStatus.BAD_REQUEST);
                }

                @Test
                @DisplayName("Should return validation errors as JSON")
                void shouldReturnValidationErrorsAsJson() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .hasContentType(MediaType.APPLICATION_JSON);
                }

                @Test
                @DisplayName("Should return one validation error")
                void shouldReturnOneValidationError() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .bodyJson()
                            .extractingPath("$.fieldErrors.length()")
                            .isEqualTo(1);
                }

                @Test
                @DisplayName("Should return a validation error about empty title")
                void shouldReturnValidationErrorAboutEmptyTitle() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .bodyJson()
                            .extractingPath("$.fieldErrors[?(@.field == 'title')].errorCode")
                            .asArray()
                            .containsExactly(VALIDATION_ERROR_CODE_EMPTY_VALUE);
                }

                @Test
                @DisplayName("Shouldn't create a new todo item")
                void shouldNotCreateNewTodoItem() {
                    requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES);

                    verify(service, never()).create(any());
                }
            }

            @Nested
            @DisplayName("When the field values are too long")
            class WhenFieldValuesAreTooLong {

                private final Map<String, Object> TEMPLATE_VARIABLES = Map.of(
                        TEMPLATE_VARIABLE_DESCRIPTION, WebTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION + 1),
                        TEMPLATE_VARIABLE_TITLE, WebTestUtil.createStringWithLength(MAX_LENGTH_TITLE + 1)
                );

                @Test
                @DisplayName("Should return the HTTP status code bad request (400)")
                void shouldReturnHttpStatusCodeBadRequest() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .hasStatus(HttpStatus.BAD_REQUEST);
                }

                @Test
                @DisplayName("Should return validation errors as JSON")
                void shouldReturnValidationErrorsAsJson() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .hasContentType(MediaType.APPLICATION_JSON);
                }

                @Test
                @DisplayName("Should return two validation error")
                void shouldReturnTwoValidationErrors() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .bodyJson()
                            .extractingPath("$.fieldErrors.length()")
                            .isEqualTo(2);
                }

                @Test
                @DisplayName("Should return a validation error about too long description")
                void shouldReturnValidationErrorAboutTooLongDescription() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .bodyJson()
                            .extractingPath("$.fieldErrors[?(@.field == 'description')].errorCode")
                            .asArray()
                            .containsExactly(VALIDATION_ERROR_CODE_TOO_LONG_VALUE);
                }

                @Test
                @DisplayName("Should return a validation error about too long title")
                void shouldReturnValidationErrorAboutTooLongTitle() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .bodyJson()
                            .extractingPath("$.fieldErrors[?(@.field == 'title')].errorCode")
                            .asArray()
                            .containsExactly(VALIDATION_ERROR_CODE_TOO_LONG_VALUE);
                }

                @Test
                @DisplayName("Shouldn't create a new todo item")
                void shouldNotCreateNewTodoItem() {
                    requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES);

                    verify(service, never()).create(any());
                }
            }
        }

        @Nested
        @DisplayName("When the information of the created todo item is valid")
        class WhenValidInformationIsProvided {

            private final String DESCRIPTION = WebTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);
            private final Long ID = 1L;
            private final String TITLE = WebTestUtil.createStringWithLength(MAX_LENGTH_TITLE);

            private final String EXPECTED_RESPONSE_BODY_JSON = String.format("""
                    {
                        "id": 1,
                        "description": "%s",
                        "tags": [],
                        "title": "%s",
                        "status": "OPEN"
                    }
                    """, DESCRIPTION, TITLE);

            @Nested
            @DisplayName("When the created todo item doesn't have unknown properties")
            class WhenCreatedTodoItemDoesNotHaveUnknownProperties {

                private final Map<String, Object> TEMPLATE_VARIABLES = Map.of(
                        TEMPLATE_VARIABLE_DESCRIPTION, DESCRIPTION,
                        TEMPLATE_VARIABLE_TITLE, TITLE
                );

                @BeforeEach
                void returnCreatedTodoItem() {
                    TodoItemDTO created = new TodoItemDTO();
                    created.setId(ID);
                    created.setDescription(DESCRIPTION);
                    created.setStatus(TodoItemStatus.OPEN);
                    created.setTags(new ArrayList<>());
                    created.setTitle(TITLE);

                    given(service.create(any())).willReturn(created);
                }

                @Test
                @DisplayName("Should return the HTTP status status code created (201)")
                void shouldReturnHttpStatusCodeCreated() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .hasStatus(HttpStatus.CREATED);
                }

                @Test
                @DisplayName("Should return the information of the created todo item as JSON")
                void shouldReturnInformationOfCreatedTodoItemAsJSON() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .hasContentType(MediaType.APPLICATION_JSON);
                }

                @Test
                @DisplayName("Should return the information of the created todo item")
                void shouldReturnInformationOfCreatedTodoItem() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES))
                            .bodyJson()
                            .isStrictlyEqualTo(EXPECTED_RESPONSE_BODY_JSON);
                }

                @Test
                @DisplayName("Should create a new todo item with the correct description")
                void shouldCreateNewTodoItemWithCorrectDescription() {
                    requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES);
                    verify(service, times(1)).create(assertArg(
                            created -> assertThat(created.getDescription()).isEqualTo(DESCRIPTION)
                    ));
                }

                @Test
                @DisplayName("Should create a new todo item with the correct title")
                void shouldCreateNewTodoItemWithCorrectTitle() {
                    requestBuilder.create(RequestBodyTemplate.TODO_ITEM, TEMPLATE_VARIABLES);
                    verify(service, times(1)).create(assertArg(
                            created -> assertThat(created.getTitle()).isEqualTo(TITLE)
                    ));
                }
            }

            @Nested
            @DisplayName("When the created todo item has an unknown property")
            class WhenCreatedTodoItemHasUnknownProperty {

                private static final String UNKNOWN_VALUE = "unknownValue";

                private final Map<String, Object> TEMPLATE_VARIABLES = Map.of(
                        TEMPLATE_VARIABLE_DESCRIPTION, DESCRIPTION,
                        TEMPLATE_VARIABLE_TITLE, TITLE,
                        TEMPLATE_VARIABLE_UNKNOWN, UNKNOWN_VALUE
                );

                @BeforeEach
                void returnCreatedTodoItem() {
                    TodoItemDTO created = new TodoItemDTO();
                    created.setId(ID);
                    created.setDescription(DESCRIPTION);
                    created.setStatus(TodoItemStatus.OPEN);
                    created.setTags(new ArrayList<>());
                    created.setTitle(TITLE);

                    given(service.create(any())).willReturn(created);
                }

                @Test
                @DisplayName("Should return the HTTP status status code created (201)")
                void shouldReturnHttpStatusCodeCreated() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM_WITH_UNKNOWN_PROPERTY, TEMPLATE_VARIABLES))
                            .hasStatus(HttpStatus.CREATED);
                }

                @Test
                @DisplayName("Should return the information of the created todo item as JSON")
                void shouldReturnInformationOfCreatedTodoItemAsJSON() {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM_WITH_UNKNOWN_PROPERTY, TEMPLATE_VARIABLES))
                            .hasContentType(MediaType.APPLICATION_JSON);
                }

                @Test
                @DisplayName("Should return the information of the created todo item")
                void shouldReturnInformationOfCreatedTodoItem() throws Exception {
                    assertThat(requestBuilder.create(RequestBodyTemplate.TODO_ITEM_WITH_UNKNOWN_PROPERTY, TEMPLATE_VARIABLES))
                            .bodyJson()
                            .isStrictlyEqualTo(EXPECTED_RESPONSE_BODY_JSON);
                }

                @Test
                @DisplayName("Should create a new todo item with the correct description")
                void shouldCreateNewTodoItemWithCorrectDescription() {
                    requestBuilder.create(RequestBodyTemplate.TODO_ITEM_WITH_UNKNOWN_PROPERTY, TEMPLATE_VARIABLES);
                    verify(service, times(1)).create(assertArg(
                            created -> assertThat(created.getDescription()).isEqualTo(DESCRIPTION)
                    ));
                }

                @Test
                @DisplayName("Should create a new todo item with the correct title")
                void shouldCreateNewTodoItemWithCorrectTitle() {
                    requestBuilder.create(RequestBodyTemplate.TODO_ITEM_WITH_UNKNOWN_PROPERTY, TEMPLATE_VARIABLES);
                    verify(service, times(1)).create(assertArg(
                            created -> assertThat(created.getTitle()).isEqualTo(TITLE)
                    ));
                }
            }
        }
    }

    @Nested
    @DisplayName("Find all todo items")
    class FindAll {

        @Test
        @DisplayName("Should return the HTTP status code OK (200)")
        void shouldReturnHttpStatusCodeOk() {
            assertThat(requestBuilder.findAll()).hasStatusOk();
        }

        @Test
        @DisplayName("Should return the found todo items as JSON")
        void shouldReturnFoundTodoItemAsJSON() {
            assertThat(requestBuilder.findAll()).hasContentType(MediaType.APPLICATION_JSON);
        }

        @Nested
        @DisplayName("When no todo items are found")
        class WhenNoTodoItemsAreFound {

            @BeforeEach
            void returnEmptyList() {
                given(service.findAll()).willReturn(new ArrayList<>());
            }

            @Test
            @DisplayName("Should return zero todo items")
            void shouldReturnZeroTodoItems() {
                assertThat(requestBuilder.findAll())
                        .bodyJson()
                        .extractingPath("$.length()")
                        .isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("When two todo items are found")
        class WhenTwoTodoItemsAreFound {

            private static final Long FIRST_TODO_ITEM_ID = 1L;
            private static final TodoItemStatus FIRST_TODO_ITEM_STATUS = TodoItemStatus.DONE;
            private static final String FIRST_TODO_ITEM_TITLE = "Write example application";

            private static final Long SECOND_TODO_ITEM_ID = 2L;
            private static final TodoItemStatus SECOND_TODO_ITEM_STATUS = TodoItemStatus.IN_PROGRESS;
            private static final String SECOND_TODO_ITEM_TITLE = "Write blog post";

            private static final String EXPECTED_BODY_JSON = """
                    [
                        {
                            "id": 1,
                            "title": "Write example application",
                            "status": "DONE"
                        },
                        {
                            "id": 2,
                            "title": "Write blog post",
                            "status": "IN_PROGRESS"
                        }
                    ]
                    """;

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
                assertThat(requestBuilder.findAll())
                        .bodyJson()
                        .extractingPath("$.length()")
                        .isEqualTo(2);
            }

            @Test
            @DisplayName("Should return the information of found todo items")
            void shouldReturnInformationOfFoundTodoItems() {
                assertThat(requestBuilder.findAll())
                        .bodyJson()
                        .isStrictlyEqualTo(EXPECTED_BODY_JSON);
            }
        }
    }

    @Nested
    @DisplayName("Find todo item by using its id as search criteria")
    class FindById {

        private static final Long TODO_ITEM_ID = 1L;

        @Nested
        @DisplayName("When the requested todo item isn't found")
        class WhenRequestedTodoItemIsNotFound {

            @BeforeEach
            void throwException() {
                given(service.findById(TODO_ITEM_ID)).willThrow(new TodoItemNotFoundException(""));
            }

            @Test
            @DisplayName("Should return the HTTP status code not found (404)")
            void shouldReturnHttpStatusCodeNotFound() {
                assertThat(requestBuilder.findById(TODO_ITEM_ID)).hasStatus(HttpStatus.NOT_FOUND);
            }

            @Test
            @DisplayName("Should return an HTTP response which has an empty response body")
            void shouldReturnHttpResponseWhichHasEmptyResponseBody() {
                assertThat(requestBuilder.findById(TODO_ITEM_ID)).hasBodyTextEqualTo("");
            }
        }

        @Nested
        @DisplayName("When the requested todo item is found")
        class WhenRequestedTodoItemIsFound {

            private static final String DESCRIPTION = "Remember to use JUnit 5";
            private static final String EXPECTED_BODY_JSON = """
                    {
                        "id": 1,
                        "description": "Remember to use JUnit 5",
                        "tags": [
                            {
                                "id": 9,
                                "name": "Code"
                            }
                        ],
                        "title": "Write example application",
                        "status": "IN_PROGRESS"
                    }
                    """;
            private static final Long TAG_ID = 9L;
            private static final String TAG_NAME  = "Code";
            private static final String TITLE = "Write example application";
            private static final TodoItemStatus STATUS = TodoItemStatus.IN_PROGRESS;

            @BeforeEach
            void returnFoundTodoItem() {
                TodoItemDTO found = new TodoItemDTO();
                found.setId(TODO_ITEM_ID);
                found.setDescription(DESCRIPTION);
                found.setStatus(STATUS);
                found.setTitle(TITLE);

                TagDTO tag = new TagDTO();
                tag.setId(TAG_ID);
                tag.setName(TAG_NAME);
                found.setTags(Collections.singletonList(tag));

                given(service.findById(TODO_ITEM_ID)).willReturn(found);
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() {
                assertThat(requestBuilder.findById(TODO_ITEM_ID))
                        .hasStatusOk();
            }

            @Test
            @DisplayName("Should return the information of the found todo item as JSON")
            void shouldReturnInformationOfFoundTodoItemAsJSON() {
                assertThat(requestBuilder.findById(TODO_ITEM_ID))
                        .hasContentType(MediaType.APPLICATION_JSON);
            }

            @Test
            @DisplayName("Should return the information of the found todo item")
            void shouldReturnInformationOfFoundTodoItem() {
                assertThat(requestBuilder.findById(TODO_ITEM_ID))
                        .bodyJson()
                        .isStrictlyEqualTo(EXPECTED_BODY_JSON);
            }
        }
    }
}
