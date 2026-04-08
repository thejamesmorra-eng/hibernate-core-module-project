package sorokin.java.course.user;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import sorokin.java.course.account.AccountService;
import sorokin.java.course.helper.TransactionHelper;

import java.util.*;

@Component
public class UserService {

    private final Set<String> takenLogins;
    private final AccountService accountService;
    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;

    public UserService(AccountService accountService, TransactionHelper transactionHelper, SessionFactory sessionFactory) {
        this.takenLogins = new HashSet<>();
        this.accountService = accountService;
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
    }

    public User createUser(String login) {
       String normalizedLogin = validateLogin(login);
       if (takenLogins.contains(normalizedLogin)) {
           throw new IllegalArgumentException("User already exists with login=%s".formatted(normalizedLogin));
        }
        return transactionHelper.executeInTransactionOrJoin(() -> {
            var user = new User(login);
            sessionFactory.getCurrentSession().persist(user);
            var defaultAccount = accountService.createAccount(user);
            user.getAccountList().add(defaultAccount);
            takenLogins.add(login);
            return user;
        });
    }

    public User findUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User id must be > 0");
        }
        try (Session session = sessionFactory.openSession()) {
            var user = session.find(User.class, id);
            if (user == null) {
                throw new IllegalArgumentException("No such user with id=%s".formatted(id));
            }
            return user;
        }
    }

    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.accountList";
            return session.createQuery(query, User.class).getResultList();
        }
    }

    private String validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login must not be blank");
        }
        return login.trim();
    }
}
