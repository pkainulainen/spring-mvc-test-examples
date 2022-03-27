package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static net.petrikainulainen.springmvctest.junit5.web.WebTestConfig.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TodoItemCrudControllerTest {

    private StaticMessageSource messageSource = new StaticMessageSource();
    private TodoItemRequestBuilder requestBuilder;
    private TodoItemCrudService service;

    @BeforeEach
    void configureSystemUnderTest() {
        service = mock(TodoItemCrudService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new TodoItemCrudController(messageSource, service))
                .setHandlerExceptionResolvers(exceptionResolver())
                .setLocaleResolver(fixedLocaleResolver())
                .setViewResolvers(jspViewResolver())
                .build();
        requestBuilder = new TodoItemRequestBuilder(mockMvc);
    }

    @Nested
    @DisplayName("Process the create todo item form")
    class SubmitCreateTodoItemForm {

        private static final String FORM_OBJECT_ALIAS = "todoItem";
        private static final int MAX_LENGTH_DESCRIPTION = 1000;
        private static final int MAX_LENGTH_TITLE = 100;

        private CreateTodoItemFormDTO formObject;

        @Nested
        @DisplayName("When validation fails")
        class WhenValidationFails {

            private static final String FORM_FIELD_NAME_DESCRIPTION = "description";
            private static final String FORM_FIELD_NAME_TITLE = "title";

            private static final String VALIDATION_ERROR_NOT_BLANK = "NotBlank";
            private static final String VALIDATION_ERROR_SIZE = "Size";

            private static final String VIEW_NAME_FORM_VIEW = "todo-item/create";

            @Nested
            @DisplayName("When the title and description are missing")
            class WhenTitleAndDescriptionAreMissing {

                @BeforeEach
                void createFormObject() {
                    formObject = new CreateTodoItemFormDTO();
                }

                @Test
                @DisplayName("Should return the HTTP status code OK (200)")
                void shouldReturnHttpStatusCodeOk() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(status().isOk());
                }

                @Test
                @DisplayName("Should render the form view")
                void shouldRenderFormView() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(view().name(VIEW_NAME_FORM_VIEW));
                }

                @Test
                @DisplayName("Should display an empty create todo item form")
                void shouldDisplayEmptyCreateTodoItemForm() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attribute(FORM_OBJECT_ALIAS, allOf(
                                    hasProperty(FORM_FIELD_NAME_DESCRIPTION, nullValue()),
                                    hasProperty(FORM_FIELD_NAME_TITLE, nullValue())
                            )));
                }

                @Test
                @DisplayName("Should display one validation error")
                void shouldDisplayOneValidationError() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attributeErrorCount(FORM_OBJECT_ALIAS, 1));
                }

                @Test
                @DisplayName("Should display a validation error about missing title")
                void shouldDisplayValidationErrorAboutMissingTitle() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attributeHasFieldErrorCode(
                                    FORM_OBJECT_ALIAS,
                                    FORM_FIELD_NAME_TITLE,
                                    VALIDATION_ERROR_NOT_BLANK
                            ));
                }

                @Test
                @DisplayName("Shouldn't create a new todo item")
                void shouldNotCreateNewTodoItem() throws Exception {
                    requestBuilder.create(formObject);
                    verify(service, never()).create(isA(CreateTodoItemFormDTO.class));
                }
            }

            @Nested
            @DisplayName("When the title and description are empty strings")
            class WhenTitleAndDescriptionAreEmptyStrings {

                @BeforeEach
                void createFormObject() {
                    formObject = new CreateTodoItemFormDTO();
                    formObject.setDescription("");
                    formObject.setTitle("");
                }

                @Test
                @DisplayName("Should return the HTTP status code OK (200)")
                void shouldReturnHttpStatusCodeOk() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(status().isOk());
                }

                @Test
                @DisplayName("Should render the form view")
                void shouldRenderFormView() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(view().name(VIEW_NAME_FORM_VIEW));
                }

                @Test
                @DisplayName("Should display an empty create todo item form")
                void shouldDisplayEmptyCreateTodoItemForm() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attribute(FORM_OBJECT_ALIAS, allOf(
                                    hasProperty(FORM_FIELD_NAME_DESCRIPTION, is(emptyString())),
                                    hasProperty(FORM_FIELD_NAME_TITLE, is(emptyString()))
                            )));
                }

                @Test
                @DisplayName("Should display one validation error")
                void shouldDisplayOneValidationError() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attributeErrorCount(FORM_OBJECT_ALIAS, 1));
                }

                @Test
                @DisplayName("Should display a validation error about empty title")
                void shouldDisplayValidationErrorAboutEmptyTitle() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attributeHasFieldErrorCode(
                                    FORM_OBJECT_ALIAS,
                                    FORM_FIELD_NAME_TITLE,
                                    VALIDATION_ERROR_NOT_BLANK
                            ));
                }

                @Test
                @DisplayName("Shouldn't create a new todo item")
                void shouldNotCreateNewTodoItem() throws Exception {
                    requestBuilder.create(formObject);
                    verify(service, never()).create(isA(CreateTodoItemFormDTO.class));
                }
            }

            @Nested
            @DisplayName("When the title and descriptin contain only whitespace characters")
            class WhenTitleAndDescriptionContainOnlyWhiteSpaceCharacters {

                private static final String DESCRIPTION = "    ";
                private static final String TITLE = "               ";

                @BeforeEach
                void createFormObject() {
                    formObject = new CreateTodoItemFormDTO();
                    formObject.setDescription(DESCRIPTION);
                    formObject.setTitle(TITLE);
                }

                @Test
                @DisplayName("Should return the HTTP status code OK (200)")
                void shouldReturnHttpStatusCodeOk() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(status().isOk());
                }

                @Test
                @DisplayName("Should render the form view")
                void shouldRenderFormView() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(view().name(VIEW_NAME_FORM_VIEW));
                }

                @Test
                @DisplayName("Should display the createtodo item form with correct description and title")
                void shouldDisplayCreateTodoItemFormWithCorrectDescriptionAndTitle() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attribute(FORM_OBJECT_ALIAS, allOf(
                                    hasProperty(FORM_FIELD_NAME_DESCRIPTION, equalTo(DESCRIPTION)),
                                    hasProperty(FORM_FIELD_NAME_TITLE, equalTo(TITLE))
                            )));
                }

                @Test
                @DisplayName("Should display one validation error")
                void shouldDisplayOneValidationError() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attributeErrorCount(FORM_OBJECT_ALIAS, 1));
                }

                @Test
                @DisplayName("Should display a validation error about empty title")
                void shouldDisplayValidationErrorAboutEmptyTitle() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attributeHasFieldErrorCode(
                                    FORM_OBJECT_ALIAS,
                                    FORM_FIELD_NAME_TITLE,
                                    VALIDATION_ERROR_NOT_BLANK
                            ));
                }

                @Test
                @DisplayName("Shouldn't create a new todo item")
                void shouldNotCreateNewTodoItem() throws Exception {
                    requestBuilder.create(formObject);
                    verify(service, never()).create(isA(CreateTodoItemFormDTO.class));
                }
            }

            @Nested
            @DisplayName("When the title and description are too long")
            class WhenTitleAndDescriptionAreTooLong {

                private static final String TOO_LONG_DESCRIPTION = WebTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION + 1);
                private static final String TOO_LONG_TITLE = WebTestUtil.createStringWithLength(MAX_LENGTH_TITLE + 1);

                @BeforeEach
                void createFormObject() {
                    formObject = new CreateTodoItemFormDTO();
                    formObject.setDescription(TOO_LONG_DESCRIPTION);
                    formObject.setTitle(TOO_LONG_TITLE);
                }

                @Test
                @DisplayName("Should return the HTTP status code OK (200)")
                void shouldReturnHttpStatusCodeOk() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(status().isOk());
                }

                @Test
                @DisplayName("Should render the form view")
                void shouldRenderFormView() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(view().name(VIEW_NAME_FORM_VIEW));
                }

                @Test
                @DisplayName("Should display a create todo item form that contains the invalid information")
                void shouldDisplayCreateTodoItemFormThatContainsInvalidInformation() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attribute(FORM_OBJECT_ALIAS, allOf(
                                    hasProperty(FORM_FIELD_NAME_DESCRIPTION, equalTo(TOO_LONG_DESCRIPTION)),
                                    hasProperty(FORM_FIELD_NAME_TITLE, equalTo(TOO_LONG_TITLE))
                            )));
                }

                @Test
                @DisplayName("Should display two validation errors")
                void shouldDisplayOneValidationError() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attributeErrorCount(FORM_OBJECT_ALIAS, 2));
                }

                @Test
                @DisplayName("Should display a validation error about too long description")
                void shouldDisplayValidationErrorAboutTooLongDescription() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attributeHasFieldErrorCode(
                                    FORM_OBJECT_ALIAS,
                                    FORM_FIELD_NAME_DESCRIPTION,
                                    VALIDATION_ERROR_SIZE
                            ));
                }

                @Test
                @DisplayName("Should display a validation error about too long title")
                void shouldDisplayValidationErrorAboutTooLongTitle() throws Exception {
                    requestBuilder.create(formObject)
                            .andExpect(model().attributeHasFieldErrorCode(
                                    FORM_OBJECT_ALIAS,
                                    FORM_FIELD_NAME_TITLE,
                                    VALIDATION_ERROR_SIZE
                            ));
                }

                @Test
                @DisplayName("Shouldn't create a new todo item")
                void shouldNotCreateNewTodoItem() throws Exception {
                    requestBuilder.create(formObject);
                    verify(service, never()).create(isA(CreateTodoItemFormDTO.class));
                }
            }
        }

        @Nested
        @DisplayName("When validation is successful")
        class WhenValidationIsSuccessful {

            private static final String FEEDBACK_MESSAGE = "A new todo item was created";
            private static final String FEEDBACK_MESSAGE_KEY = "feedback.message.todoItem.created";

            private static final String FLASH_ATTRIBUTE_KEY_FEEDBACK_MESSAGE = "feedbackMessage";

            private static final String MODEL_ATTRIBUTE_NAME_ID = "id";
            private static final String VIEW_NAME_VIEW_TODO_ITEM_VIEW = "redirect:/todo-item/{id}";

            private static final Long ID = 1L;
            private static final String DESCRIPTION = WebTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);
            private static final String TITLE = WebTestUtil.createStringWithLength(MAX_LENGTH_TITLE);

            @BeforeEach
            void configureSystemUnderTest() {
                formObject = createFormObject();
                configureFeedbackMessage();
                returnCreatedTodoItem();
            }

            private CreateTodoItemFormDTO createFormObject() {
                CreateTodoItemFormDTO formObject = new CreateTodoItemFormDTO();
                formObject.setDescription(DESCRIPTION);
                formObject.setTitle(TITLE);
                return formObject;
            }

            private void configureFeedbackMessage() {
                messageSource.addMessage(
                        FEEDBACK_MESSAGE_KEY,
                        WebTestConfig.LOCALE,
                        FEEDBACK_MESSAGE
                );
            }

            private void returnCreatedTodoItem() {
                TodoItemDTO created = new TodoItemDTO();
                created.setId(ID);

                given(service.create(any())).willReturn(created);
            }

            @Test
            @DisplayName("Should return the HTTP status code found (302)")
            void shouldReturnHttpStatusCodeFound() throws Exception {
                requestBuilder.create(formObject)
                        .andExpect(status().isFound());
            }

            @Test
            @DisplayName("Should redirect the user to the view todo item view")
            void shouldRedirectUserToViewTodoItemView() throws Exception {
                requestBuilder.create(formObject)
                        .andExpect(view().name(VIEW_NAME_VIEW_TODO_ITEM_VIEW))
                        .andExpect(model().attribute(MODEL_ATTRIBUTE_NAME_ID, equalTo(ID.toString())));
            }

            @Test
            @DisplayName("Should create a new flash attribute that contains the correct feedback message")
            void shouldCreateNewFlashAttributeThatContainsCorrectFeedbackMessage() throws Exception {
                requestBuilder.create(formObject)
                        .andExpect(flash().attribute(
                                FLASH_ATTRIBUTE_KEY_FEEDBACK_MESSAGE,
                                equalTo(FEEDBACK_MESSAGE)
                        ));
            }

            @Test
            @DisplayName("Should create a new todo item with the correct description")
            void shouldCreateNewTodoItemWithCorrectDescription() throws Exception {
                requestBuilder.create(formObject);

                verify(service, times(1)).create(assertArg(
                        todoItem -> assertThat(todoItem.getDescription())
                                .isEqualTo(DESCRIPTION)
                ));
            }

            @Test
            @DisplayName("Should create a new todo item with the correct title")
            void shouldCreateNewTodoItemWithCorrectTitle() throws Exception {
                requestBuilder.create(formObject);

                verify(service, times(1)).create(assertArg(
                        todoItem -> assertThat(todoItem.getTitle())
                                .isEqualTo(TITLE)
                ));
            }
        }
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
