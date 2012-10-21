package net.petrikainulainen.spring.testmvc.todo.repository;

import net.petrikainulainen.spring.testmvc.todo.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Petri Kainulainen
 */
public interface ToDoRepository extends JpaRepository<ToDo, Long> {
}
