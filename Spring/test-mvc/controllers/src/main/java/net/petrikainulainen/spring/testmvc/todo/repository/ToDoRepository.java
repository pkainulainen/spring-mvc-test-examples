package net.petrikainulainen.spring.testmvc.todo.repository;

import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Petri Kainulainen
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
