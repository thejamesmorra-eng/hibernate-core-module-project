package sorokin.java.course.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class TransactionHelper {

    private final SessionFactory sessionFactory;

    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeInTransactionOrJoin(Supplier<T> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.getTransaction();
        boolean owner = tx.getStatus() == TransactionStatus.NOT_ACTIVE;

        if (owner) {
            tx = session.beginTransaction();
        }
        try {
            T result = action.get();
            if (owner) {
                tx.commit();
            }
            return result;
        } catch (RuntimeException e) {
            if (owner) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (owner) {
                session.close();
            }
        }
    }

    public void executeInTransactionOrJoin(Runnable action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.getTransaction();
        boolean owner = tx.getStatus() == TransactionStatus.NOT_ACTIVE;

        if (owner) {
            tx = session.beginTransaction();
        }
        try {
            action.run();
            if (owner) {
                tx.commit();
            }
        } catch (RuntimeException e) {
            if (owner) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (owner) {
                session.close();
            }
        }
    }

//    public void executeInTransaction(Consumer<Session> action) {
//        Transaction transaction = null;
//        try (Session session = sessionFactory.openSession()) {
//            transaction = session.getTransaction();
//            transaction.begin();
//
//            action.accept(session);
//
//            transaction.commit();
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            throw e;
//        }
//    }
//
//    public <T> T executeInTransaction(Function<Session, T> action) {
//        Transaction transaction = null;
//        try (Session session = sessionFactory.openSession()) {
//            transaction = session.getTransaction();
//            transaction.begin();
//
//            var result = action.apply(session);
//
//            transaction.commit();
//            return result;
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            throw e;
//        }
//    }
}
