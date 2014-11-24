package net.petrikainulainen.spring.datajpa;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import net.petrikainulainen.spring.datajpa.model.Todo;
import net.petrikainulainen.spring.datajpa.service.TodoService;
import org.hibernate.Transaction;
import org.hibernate.ejb.TransactionImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {PersistenceContext.class})
@ContextConfiguration(locations = {"classpath:exampleApplicationContext-persistence.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("todoData.xml")
public class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private JpaTransactionManager transactionManager;


    @Test
    public void testReadOnly() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        //返回一个已经激活的事务或创建一个新的事务（根据给定的TransactionDefinition类型参数定义的事务属性），
        // 返回的是TransactionStatus对象代表了当前事务的状态
        TransactionStatus status = transactionManager.getTransaction(def);
        //  Transaction ta=
        Todo todo= todoService.findById(38l);
        assertThat(todo, allOf(hasProperty("title", is("Foo"))));

        //todo: 修改数据库，commit
        Todo todo2= todoService.findById(1l);
        assertThat(todo2, allOf(hasProperty("title",  is("Foo"))));

        transactionManager.commit(status);
    }



    /*todo：如果在只读事物里更新数据库？*/
    @Test
    public void testSave() {
        Todo todo = new Todo("test", "tttt");
        todo = todoService.save(todo);
    }

    @Test
    public void testSupport() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("test-1");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        Todo todo = new Todo("test","tttt");

        todo= todoService.save(todo);
        //transactionManager.commit(status);
        transactionManager.rollback(status);
    }

    @Test
    public void testNewSupport() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("test-1");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        Todo todo = new Todo("test","tttt");

        todo= todoService.save2(todo);
        //transactionManager.commit(status);
        transactionManager.rollback(status);
    }


    @Test
    public void testUpdate() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        Todo todo = new Todo("test", "tttt");
        todo.setId(1L);
        todo = todoService.update(todo);

        System.out.println(status.isCompleted());
        transactionManager.commit(status);
        System.out.println(status.isCompleted());
    }

    /*测试NESTED级别*/
    @Test
    public void testNested() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        Todo todo = new Todo("test","tttt");
        todoService.updateNested(todo);

        new RuntimeException("test runtime exception") ;
        //transactionManager.rollback(status);
        transactionManager.commit(status);

    }


    /*测试隔离级别*/
    @Test
    public void testIsolation() {
       List list = todoService.queryAll();

        System.out.println(list.size());
       List list2 = todoService.queryAll();

        System.out.println(list2.size());
    }

}
