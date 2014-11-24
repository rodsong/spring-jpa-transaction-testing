package net.petrikainulainen.spring.datajpa.service;

import net.petrikainulainen.spring.datajpa.model.Todo;
import net.petrikainulainen.spring.datajpa.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rodsong
 * Date: 2014-19-16 16:33
 * To change
 */

@Service
public class TodoServiceImp implements TodoService {

    @Autowired
    private TodoRepository repository;


    @Transactional(readOnly = false)
    public Todo findById(Long id) {
        return repository.findOne(id);
    }

    //todo:这么配置有问题吗？
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public Todo findById2(Long id) {

        return repository.findOne(id);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, readOnly = true)
    public Todo save(Todo todo) {
        todo = repository.save(todo);
        //todo = repository.findOne(todo.getId());
       return todo;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public Todo save2(Todo todo) {
        return repository.save(todo);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public Todo update(Todo todo) {
        return repository.saveAndFlush(todo);
    }


    @Transactional(propagation= Propagation.NESTED)
    public void updateNested(Todo todo) {
        repository.save(todo);
        new RuntimeException("test runtime exception") ;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.READ_UNCOMMITTED)
    public List<Todo> queryAll() {
        return repository.findAll();
    }
}
