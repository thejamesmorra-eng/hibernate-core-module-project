package sorokin.java.course.user;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import sorokin.java.course.account.AccountService;
import sorokin.java.course.helper.TransactionHelper;
import sorokin.java.course.user.User;

import java.util.*;

@Component
public class UserService {

    private int idCounter;
    private final Map<Integer, User> userMap;
    private final Set<String> takenLogins;
    private final AccountService accountService;
    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;

    public UserService(AccountService accountService, TransactionHelper transactionHelper, SessionFactory sessionFactory) {
        this.idCounter = 0;
        this.userMap = new HashMap<>();
        this.takenLogins = new HashSet<>();
        this.accountService = accountService;
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
    }

    public User createUser(String login) {
//        String normalizedLogin = validateLogin(login);
//        if (takenLogins.contains(normalizedLogin)) {
//            throw new IllegalArgumentException("User already exists with login=%s".formatted(normalizedLogin));
//        }

        return transactionHelper.executeInTransactionOrJoin(() -> {
            var user = new User(login);
            sessionFactory.getCurrentSession().persist(user);
            var defaultAccount = accountService.createAccount(user);
            user.getAccountList().add(defaultAccount);

            userMap.put(user.getId(), user);
            takenLogins.add(login);
            return user;
        });
    }

    public User findUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("user id must be > 0");
        }
        var user = userMap.get(id);
        if (user == null) {
            throw new IllegalArgumentException("No such user with id=%s".formatted(id));
        }
        return user;
    }

    public List<User> findAll() {
        return userMap.values().stream().toList();
    }

    private String validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login must not be blank");
        }
        return login.trim();
    }
}
