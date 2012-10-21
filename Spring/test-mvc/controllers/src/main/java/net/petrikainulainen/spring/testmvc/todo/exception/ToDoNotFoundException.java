package net.petrikainulainen.spring.testmvc.todo.exception;

/**
 * @author Petri Kainulainen
 */
public class ToDoNotFoundException extends Exception {

    public ToDoNotFoundException(String message) {
        super(message);
    }
}
