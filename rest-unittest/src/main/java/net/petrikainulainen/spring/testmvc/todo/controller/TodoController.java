package net.petrikainulainen.spring.testmvc.todo.controller;

import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.TodoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import net.petrikainulainen.spring.testmvc.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Petri Kainulainen
 */
@Controller
public class TodoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoController.class);

    private TodoService service;

    @Autowired
    public TodoController(TodoService service) {
        this.service = service;
    }

    @RequestMapping(value = "/api/todo", method = RequestMethod.POST)
    @ResponseBody
    public TodoDTO add(@Valid @RequestBody TodoDTO dto) {
        LOGGER.debug("Adding a new to-do entry with information: {}", dto);

        Todo added = service.add(dto);
        LOGGER.debug("Added a to-do entry with information: {}", added);

       return createDTO(added);
    }

    @RequestMapping(value = "/api/todo/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public TodoDTO deleteById(@PathVariable("id") Long id) throws TodoNotFoundException {
        LOGGER.debug("Deleting a to-do entry with id: {}", id);

        Todo deleted = service.deleteById(id);
        LOGGER.debug("Deleted to-do entry with information: {}", deleted);

        return createDTO(deleted);
    }

    @RequestMapping(value = "/api/todo", method = RequestMethod.GET)
    @ResponseBody
    public List<TodoDTO> findAll() {
        LOGGER.debug("Finding all todo entries.");

        List<Todo> models = service.findAll();
        LOGGER.debug("Found {} to-do entries.", models.size());

        return createDTOs(models);
    }

    private List<TodoDTO> createDTOs(List<Todo> models) {
        List<TodoDTO> dtos = new ArrayList<TodoDTO>();

        for (Todo model: models) {
            dtos.add(createDTO(model));
        }

        return dtos;
    }

    @RequestMapping(value = "/api/todo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public TodoDTO findById(@PathVariable("id") Long id) throws TodoNotFoundException {
        LOGGER.debug("Finding to-do entry with id: {}", id);

        Todo found = service.findById(id);
        LOGGER.debug("Found to-do entry with information: {}", found);

        return createDTO(found);
    }

    @RequestMapping(value = "/api/todo/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public TodoDTO update(@Valid @RequestBody TodoDTO dto, @PathVariable("id") Long todoId) throws TodoNotFoundException {
        LOGGER.debug("Updating a to-do entry with information: {}", dto);

        Todo updated = service.update(dto);
        LOGGER.debug("Updated the information of a to-entry to: {}", updated);

        return createDTO(updated);
    }

    private TodoDTO createDTO(Todo model) {
        TodoDTO dto = new TodoDTO();

        dto.setId(model.getId());
        dto.setDescription(model.getDescription());
        dto.setTitle(model.getTitle());

        return dto;
    }
}
