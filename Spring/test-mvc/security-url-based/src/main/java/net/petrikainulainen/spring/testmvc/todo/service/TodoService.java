package net.petrikainulainen.spring.testmvc.todo.service;

import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.TodoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;

import java.util.List;

/**
 * @author Petri Kainulainen
 */
public interface TodoService {

    /**
     * Adds a new to-do entry.
     * @param added The information of the added to-do entry.
     * @return  The added to-do entry.
     */
    public Todo add(TodoDTO added);

    /**
     * Deletes a to-do entry.
     * @param id    The id of the deleted to-do entry.
     * @return  The deleted to-do entry.
     * @throws TodoNotFoundException    if no to-do entry is found with the given id.
     */
    public Todo deleteById(Long id) throws TodoNotFoundException;

    /**
     * Returns a list of to-do entries.
     * @return
     */
    public List<Todo> findAll();

    /**
     * Finds a to-do entry.
     * @param id    The id of the wanted to-do entry.
     * @return  The found to-entry.
     * @throws TodoNotFoundException    if no to-do entry is found with the given id.
     */
    public Todo findById(Long id) throws TodoNotFoundException;

    /**
     * Updates the information of a to-do entry.
     * @param updated   The information of the updated to-do entry.
     * @return  The updated to-do entry.
     * @throws TodoNotFoundException    If no to-do entry is found with the given id.
     */
    public Todo update(TodoDTO updated) throws TodoNotFoundException;
}
