package net.petrikainulainen.spring.datajpa.service;

import net.petrikainulainen.spring.datajpa.model.Todo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rodsong
 * Date: 2014-19-16 16:32
 * To change
 */
public interface TodoService {


    Todo findById(Long id);

    Todo findById2(Long id);

    Todo save(Todo todo);

    Todo save2(Todo todo);

    Todo update(Todo todo);

    void updateNested(Todo todo);

    List<Todo> queryAll();
}
