package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.CreateTodoItemDTO;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemCrudService;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemDTO;
import net.petrikainulainen.springmvctest.junit5.todo.TodoListItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;

/**
 * Provides CRUD operations for todo items.
 */
@Controller
@RequestMapping("/todo-item")
public class TodoItemCrudController {
    private final TodoItemCrudService service;

    @Autowired
    public TodoItemCrudController(TodoItemCrudService service) {
        this.service = service;
    }

    /**
     * Creates a new todo item.
     * @param input The information of the new todo item.
     * @return  The information of the created todo item.
     */
    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public TodoItemDTO create(@RequestBody @Valid CreateTodoItemDTO input) {
        return service.create(input);
    }

    /**
     * Finds the information of todo items found from the database.
     *
     * @return  The todo items found from the database. If no todo
     *          items is found from the database, this method returns
     *          an empty list.
     */
    @GetMapping
    @ResponseBody
    public List<TodoListItemDTO> findAll() {
        return service.findAll();
    }

    /**
     * Finds the information of the specified todo item.
     *
     * @param id    The id of the requested todo item.
     * @return      The information of the found todo item.
     */
    @GetMapping("{id}")
    @ResponseBody
    public TodoItemDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }
}
