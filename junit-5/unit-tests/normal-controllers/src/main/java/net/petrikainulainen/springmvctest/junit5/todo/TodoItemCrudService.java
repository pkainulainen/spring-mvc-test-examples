package net.petrikainulainen.springmvctest.junit5.todo;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *  Declares the methods which provide CRUD operations
 *  for todo items.
 */
@Service
public class TodoItemCrudService {

    /**
     * Finds all todo items from the database.
     * @return  A list that contains the information of the found
     *          todo items. If no todo items is found, this method
     *          returns an empty list.
     * @return
     */
    public List<TodoListItemDTO> findAll() {
        return new ArrayList<>();
    }

    /**
     * Finds the requested todo item from the database.
     * @param id    The id of the todo item.
     * @return      The information of the found todo item.
     * @throws  TodoItemNotFoundException   if the requested todo item isn't found
     *                                      from the database.
     */
    public TodoItemDTO findById(Long id) {
        return null;
    }
}
