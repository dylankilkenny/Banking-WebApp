package com.example.jpa.models;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table
@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Account.class})

public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private int age;
    private String address;
    private String email;
    private int pin;
    
        public User(String name, int age, String address, String email, int pin) {
            accountlist = new ArrayList<>();
            this.name = name;
            this.age = age;
            this.address = address;
            this.email = email;
            this.pin = pin;
    }
    
    public User() {
        accountlist = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = Account.class, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Account> accountlist;

    public void setAccounts(List<Account> accountlist) {
        this.accountlist = accountlist;
    }

    @XmlElementWrapper(name = "accounts")
    @XmlElementRef()
    public List<Account> getAccounts() {
        return accountlist;
    }
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = API.class, cascade = CascadeType.ALL, mappedBy = "user")
    private List<API> apilist;

    public void setAPI(List<API> apilist) {
        this.apilist = apilist;
    }

    @XmlElementWrapper(name = "api")
    @XmlElementRef()
    public List<API> getAPI() {
        return apilist;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + ", email=" + email + ", address=" + address + ", age=" + age + "}";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

}
