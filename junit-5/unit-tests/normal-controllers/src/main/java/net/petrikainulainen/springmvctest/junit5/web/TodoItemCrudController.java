package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.TodoItemCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Provides CRUD operations for todo items.
 */
@Controller
public class TodoItemCrudController {

    private final TodoItemCrudService service;

    @Autowired
    public TodoItemCrudController(TodoItemCrudService service) {
        this.service = service;
    }
}
