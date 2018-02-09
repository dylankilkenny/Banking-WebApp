package com.example.jpa.models;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table
@XmlRootElement
public class Account implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    
    private int id;
    private String type;
    private int sortcode;
    private int accountnumber;
    private double balance;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSortcode() {
        return sortcode;
    }

    public void setSortcode(int sortcode) {
        this.sortcode = sortcode;
    }

    public int getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(int accountnumber) {
        this.accountnumber = accountnumber;
    }
    
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;

    @XmlTransient
    public User getUser() {
        return user;
    }
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = Transaction.class, cascade = CascadeType.ALL, mappedBy = "account")
    private List<Transaction> transactionlist;

    public void setTransaction(List<Transaction> transactionlist) {
        this.transactionlist = transactionlist;
    }

    @XmlElementWrapper(name = "accounts")
    @XmlElementRef()
    public List<Transaction> getTransaction() {
        return transactionlist;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "{" + "id=" + id + ", accountnumber=" + accountnumber + ", sortcode=" + sortcode + ", balance=" + balance + ", type=" + type + "}";
    }
    
}
