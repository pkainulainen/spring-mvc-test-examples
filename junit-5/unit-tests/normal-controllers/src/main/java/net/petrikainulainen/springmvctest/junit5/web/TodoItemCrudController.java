package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.TodoItemCrudService;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
     * Renders the HTML view that displays the information of the
     * requested todo item.
     * @param id    The id of the requested todo item.
     * @param model The model that contains the attributes which are
     *              required to render the HTML view.
     * @return      The name of the rendered HTML view.
     * @throws net.petrikainulainen.springmvctest.junit5.todo.TodoItemNotFoundException if the requested todo item isn't found from the database.
     */
    @GetMapping("{id}")
    public String findById(@PathVariable("id") Long id, Model model) {
        TodoItemDTO found = service.findById(id);
        model.addAttribute("todoItem", found);
        return "todo-item/view";
    }
}
