package com.budgeteer.api.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ynab_accounts")
public class YnabAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @OneToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "account_id")
    private Account account;

    @NotNull
    @Column(name = "ynab_id")
    private String ynabId;

    public YnabAccount() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getYnabId() {
        return ynabId;
    }

    public void setYnabId(String ynabId) {
        this.ynabId = ynabId;
    }
}
