package sorokin.java.course.account;

import jakarta.persistence.*;
import sorokin.java.course.user.User;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "money_amount")
    private int moneyAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Account() {
    }

    public Account(User user, int moneyAmount) {
        this.user = user;
        this.moneyAmount = moneyAmount;
    }

    public int getId() {
        return id;
    }

    public Integer getUserId() {
        return user.getId();
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(int moneyAmount) {
        if (moneyAmount < 0) {
            throw new IllegalArgumentException("Attempted to set moneyAmount less than 0");
        }
        this.moneyAmount = moneyAmount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", moneyAmount=" + moneyAmount +
                '}';
    }
}
