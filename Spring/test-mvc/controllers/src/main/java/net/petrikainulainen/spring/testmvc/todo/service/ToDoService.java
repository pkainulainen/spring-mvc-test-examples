package net.petrikainulainen.spring.testmvc.todo.service;

import net.petrikainulainen.spring.testmvc.todo.dto.ToDoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.ToDoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.ToDo;

import java.util.List;

/**
 * @author Petri Kainulainen
 */
public interface ToDoService {

    /**
     * Adds a new to-do entry.
     * @param added The information of the added to-do entry.
     * @return  The added to-do entry.
     */
    public ToDo add(ToDoDTO added);

    /**
     * Deletes a to-do entry.
     * @param id    The id of the deleted to-do entry.
     * @return  The deleted to-do entry.
     * @throws ToDoNotFoundException    if no to-do entry is found with the given id.
     */
    public ToDo deleteById(Long id) throws ToDoNotFoundException;

    /**
     * Returns a list of to-do entries.
     * @return
     */
    public List<ToDo> findAll();

    /**
     * Finds a to-do entry.
     * @param id    The id of the wanted to-do entry.
     * @return  The found to-entry.
     * @throws ToDoNotFoundException    if no to-do entry is found with the given id.
     */
    public ToDo findById(Long id) throws ToDoNotFoundException;

    /**
     * Updates the information of a to-do entry.
     * @param updated   The information of the updated to-do entry.
     * @return  The updated to-do entry.
     * @throws ToDoNotFoundException    If no to-do entry is found with the given id.
     */
    public ToDo update(ToDoDTO updated) throws ToDoNotFoundException;
}
