package net.petrikainulainen.spring.datajpa.config;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jdbc.Work;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.SQLException;

public class CustomHibernateJpaDialect2 extends HibernateJpaDialect {

    private static final long serialVersionUID = 1L;

    @Override
    public Object beginTransaction(final EntityManager entityManager,
                                   final TransactionDefinition definition)
            throws PersistenceException, SQLException, TransactionException {


        if (definition.getTimeout() != TransactionDefinition.TIMEOUT_DEFAULT) {
            getSession(entityManager).getTransaction().setTimeout(
                    definition.getTimeout());
        }

        //Session的生命周期绑定在一个物理的事务（tansaction）上面。（长的事务可能跨越多个数据库事物。）
        //每个线程/事务应该从一个SessionFactory获取自己的session实例
        Session session = getSession(entityManager);
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) session.getSessionFactory();
        java.sql.Connection connection = sessionFactory.getConnectionProvider().getConnection();
        final TransactionData data = new TransactionData();
        /*prepareConnectionForTransaction 设置connection con.setReadOnly(true);*/

        Integer previousIsolationLevel = DataSourceUtils
                .prepareConnectionForTransaction(connection, definition);

        data.setPreviousIsolationLevel(previousIsolationLevel);
        data.setConnection(connection);

        entityManager.getTransaction().begin();
        Object springTransactionData = super.prepareTransaction(entityManager,
                definition.isReadOnly(), definition.getName());

        data.setSpringTransactionData(springTransactionData);

        return data;
    }

    @Override
    public void cleanupTransaction(Object transactionData) {
        super.cleanupTransaction(((TransactionData) transactionData).getSpringTransactionData());
        ((TransactionData) transactionData).resetIsolationLevel();
    }


    private static class TransactionData {

        private Object springTransactionData;
        private Integer previousIsolationLevel;
        private Connection connection;

        public TransactionData() {
        }

        public void resetIsolationLevel() {
            if (this.previousIsolationLevel != null) {
                DataSourceUtils.resetConnectionAfterTransaction(connection,
                        previousIsolationLevel);
            }
        }

        public Object getSpringTransactionData() {
            return this.springTransactionData;
        }

        public void setSpringTransactionData(Object springTransactionData) {
            this.springTransactionData = springTransactionData;
        }

        public void setPreviousIsolationLevel(Integer previousIsolationLevel) {
            this.previousIsolationLevel = previousIsolationLevel;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

    }

    private static class SessionTransactionData {
        private final Session session;
        private final FlushMode previousFlushMode;

        public SessionTransactionData(Session session, FlushMode previousFlushMode) {
            this.session = session;
            this.previousFlushMode = previousFlushMode;
        }

        public void resetFlushMode() {
            if (this.previousFlushMode != null) {
                this.session.setFlushMode(this.previousFlushMode);
            }
        }
    }

}