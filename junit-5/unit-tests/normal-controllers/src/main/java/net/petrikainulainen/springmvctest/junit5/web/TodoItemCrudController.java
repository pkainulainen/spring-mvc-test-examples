package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.CreateTodoItemFormDTO;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemCrudService;
import net.petrikainulainen.springmvctest.junit5.todo.TodoItemDTO;
import net.petrikainulainen.springmvctest.junit5.todo.TodoListItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

/**
 * Provides CRUD operations for todo items.
 */
@Controller
@RequestMapping("/todo-item")
public class TodoItemCrudController {

    private final MessageSource messageSource;
    private final TodoItemCrudService service;

    @Autowired
    public TodoItemCrudController(MessageSource messageSource, TodoItemCrudService service) {
        this.messageSource = messageSource;
        this.service = service;
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("todoItem") CreateTodoItemFormDTO form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Locale currentLocale) {
        if (bindingResult.hasErrors()) {
            return "todo-item/create";
        }

        TodoItemDTO created = service.create(form);

        addFeedbackMessage(
                redirectAttributes,
                "feedback.message.todoItem.created",
                currentLocale,
                created.getTitle()
        );

        redirectAttributes.addAttribute("id", created.getId());
        return "redirect:/todo-item/{id}";
    }

    private void addFeedbackMessage(RedirectAttributes attributes,
                                    String messageCode,
                                    Locale currentLocale,
                                    Object... messageParameters) {
        String feedbackMessage = messageSource.getMessage(messageCode,
                messageParameters,
                currentLocale
        );
        attributes.addFlashAttribute("feedbackMessage", feedbackMessage);
    }

    /**
     * Renders the HTML view that displays the information of all
     * todo items found from the database.
     * @param model The model that contains the attributes which are
     *              required to render the HTML view.
     * @return  The name of the rendered HTML view.
     */
    @GetMapping
    public String findAll(Model model) {
        List<TodoListItemDTO> todoItems = service.findAll();
        model.addAttribute("todoItems", todoItems);
        return "todo-item/list";
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
