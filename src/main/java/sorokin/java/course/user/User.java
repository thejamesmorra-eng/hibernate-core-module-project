package sorokin.java.course.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import sorokin.java.course.account.Account;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "login")
    private String login;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Account> accountList;

    public User() {
    }

    public User(String login) {
        this.login = login;
        this.accountList = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", accountList=" + accountList +
                '}';
    }
}
