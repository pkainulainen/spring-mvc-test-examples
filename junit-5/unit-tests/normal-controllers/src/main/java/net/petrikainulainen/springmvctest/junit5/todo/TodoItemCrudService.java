package net.petrikainulainen.springmvctest.junit5.todo;

import org.springframework.stereotype.Service;

/**
 *  Declares the methods which provide CRUD operations
 *  for todo items.
 */
@Service
public class TodoItemCrudService {

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
