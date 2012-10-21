package net.petrikainulainen.spring.testmvc.todo.service;

import net.petrikainulainen.spring.testmvc.todo.dto.ToDoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.ToDoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.ToDo;
import net.petrikainulainen.spring.testmvc.todo.repository.ToDoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Petri Kainulainen
 */
@Service
public class RepositoryToDoService implements ToDoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryToDoService.class);

    @Resource
    private ToDoRepository repository;

    @Transactional
    @Override
    public ToDo add(ToDoDTO added) {
        LOGGER.debug("Adding a new to-do entry with information: {}", added);

        ToDo model = ToDo.getBuilder(added.getTitle())
                .description(added.getDescription())
                .build();

        return repository.save(model);
    }

    @Transactional(rollbackFor = {ToDoNotFoundException.class})
    @Override
    public ToDo deleteById(Long id) throws ToDoNotFoundException {
        LOGGER.debug("Deleting a to-do entry with id: {}", id);

        ToDo deleted = findById(id);
        LOGGER.debug("Deleting to-do entry: {}", deleted);

        repository.delete(deleted);
        return deleted;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ToDo> findAll() {
        LOGGER.debug("Finding all to-do entries");
        return repository.findAll();
    }

    @Transactional(readOnly = true, rollbackFor = {ToDoNotFoundException.class})
    @Override
    public ToDo findById(Long id) throws ToDoNotFoundException {
        LOGGER.debug("Finding a to-do entry with id: {}", id);

        ToDo found = repository.findOne(id);
        LOGGER.debug("Found to-do entry: {}", found);

        if (found == null) {
            throw new ToDoNotFoundException("No to-entry found with id: " + id);
        }

        return found;
    }

    @Transactional(rollbackFor = {ToDoNotFoundException.class})
    @Override
    public ToDo update(ToDoDTO updated) throws ToDoNotFoundException {
        LOGGER.debug("Updating contact with information: {}", updated);

        ToDo model = findById(updated.getId());
        LOGGER.debug("Found a to-do entry: {}", model);

        model.update(updated.getDescription(), updated.getTitle());

        return model;
    }
}
