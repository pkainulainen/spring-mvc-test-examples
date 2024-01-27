package net.petrikainulainen.springmvctest.junit5.todoitem;

import org.jooq.DSLContext;
import org.jooq.Record4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static net.petrikainulainen.springmvctest.junit5.jooq.Tables.TODO_ITEM;

/**
 * Provides CRUD operations for todo items.
 */
@Repository
class TodoItemRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoItemRepository.class);

    private final DSLContext jooq;

    @Autowired
    TodoItemRepository(DSLContext jooq) {
        this.jooq = jooq;
    }

    /**
     * Inserts a new todo item into the database.
     *
     * @param input The information of the created todo item.
     * @return  The information that of the created todo item.
     */
    @Transactional
    TodoItemDTO create(CreateTodoItem input) {
        LOGGER.debug("Creating a new todo item with information: {}", input);

        var queryResult = jooq.insertInto(TODO_ITEM)
                .columns(
                        TODO_ITEM.DESCRIPTION,
                        TODO_ITEM.TITLE
                )
                .values(
                        input.getDescription(),
                        input.getTitle()
                )
                .returningResult(
                        TODO_ITEM.ID,
                        TODO_ITEM.DESCRIPTION,
                        TODO_ITEM.TITLE,
                        TODO_ITEM.STATUS
                )
                .fetchOptional().get();
        var createdTodoItem = mapQueryResultToDTO(queryResult);

        LOGGER.debug("Created a new todo item with information: {}", input);
        return createdTodoItem;
    }

    /**
     * Returns all todo items which are found from the database.
     */
    @Transactional(readOnly = true)
    List<TodoListItemDTO> findAll() {
        LOGGER.debug("Finding all todo items from the database");

        var todoItems = jooq.select(
                TODO_ITEM.ID,
                TODO_ITEM.TITLE
        )
                .from(TODO_ITEM)
                .orderBy(TODO_ITEM.ID.asc())
                .fetchInto(TodoListItemDTO.class);

        LOGGER.debug("Found {} todo items from the database", todoItems.size());
        return todoItems;
    }

    /**
     * Finds a todo item from the database by using the provided id
     * as search criteria.
     *
     * @param id    The id of the requested todo item.
     * @return      An {@link Optional} object that contains the information
     *              of the found todo item. If no todo item is found from the
     *              database, this method returns an empty {@link Optional}
     *              object.
     */
    @Transactional(readOnly = true)
    Optional<TodoItemDTO> findById(Long id) {
        LOGGER.debug("Finding a todo item by id: #{}", id);

        var queryResult = jooq.select(
                TODO_ITEM.ID,
                TODO_ITEM.DESCRIPTION,
                TODO_ITEM.TITLE,
                TODO_ITEM.STATUS
        )
                .from(TODO_ITEM)
                .where(TODO_ITEM.ID.eq(id))
                .fetchOptional();

        var todoItem = queryResult.map(this::mapQueryResultToDTO);
        LOGGER.debug("Found todo item: {} by id: #{}", todoItem, id);

        return todoItem;
    }

    /**
     * Updates the information of the specified todo item.
     *
     * @param input The new information of the updated todo item.
     * @return  An {@link Optional} object that contains the updated information
     *          of the specified todo item. If the updated todo item isn't found
     *          from the database, this method returns an empty {@link Optional}.
     */
    @Transactional
    Optional<TodoItemDTO> update(UpdateTodoItem input) {
        LOGGER.debug("Updating the information of a todo item by using the input data: {}", input);

        var queryResult = jooq.update(TODO_ITEM)
                .set(TODO_ITEM.DESCRIPTION, input.getDescription())
                .set(TODO_ITEM.TITLE, input.getTitle())
                .where(TODO_ITEM.ID.eq(input.getId()))
                .returningResult(
                        TODO_ITEM.ID,
                        TODO_ITEM.DESCRIPTION,
                        TODO_ITEM.TITLE,
                        TODO_ITEM.STATUS
                )
                .fetchOptional();

        var updatedTodoItem = queryResult.map(this::mapQueryResultToDTO);
        LOGGER.debug("Updated the information of the todo item: {}", updatedTodoItem);

        return updatedTodoItem;
    }

    private TodoItemDTO mapQueryResultToDTO(Record4<Long, String, String, String> input) {
        var dto = new TodoItemDTO();
        dto.setId(input.value1());
        dto.setDescription(input.value2());
        dto.setTitle(input.value3());
        dto.setStatus(TodoItemStatus.valueOf(input.value4()));
        return dto;
    }
}
