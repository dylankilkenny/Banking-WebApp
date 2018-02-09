package com.example.jpa.services;

import com.example.jpa.models.Account;
import com.example.jpa.models.Transaction;
import com.example.jpa.models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;

@Path("/account")
public class AccountService {

    EntityManager entityManager;

    public AccountService() {
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("test-connection");
        entityManager = emfactory.createEntityManager();
    }    
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{accountID}/transactions")
    public ArrayList<Transaction> getTransactions(@PathParam("accountID") int accountID) {
        Account acc = entityManager.find(Account.class, accountID);
        List<Transaction> trans = acc.getTransaction();
        ArrayList<Transaction> transList = new ArrayList<Transaction>();
        for(int i = 0; i < trans.size(); i++){
            Transaction newT = new Transaction();
            Transaction t = trans.get(i);
            newT.setAmount(t.getAmount());
            newT.setId(t.getId());
            newT.setType(t.getType());
            transList.add(newT);
        }
        return transList;
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{accountID}/transaction/{transID}")
    public Transaction getTransaction(@PathParam("accountID") int accountID, @PathParam("transID") int transID) {

        Transaction trans = entityManager.find(Transaction.class, transID);
        return trans;
    }

    @DELETE
    @Path("{id}")
    // curl -v -X DELETE -H "API_KEY:VALID_KEY" http://localhost:8080/api/planets/1
    public Response deleteAccount(@PathParam("id") int id) {
        Account a = entityManager.find(Account.class, id);
        
        if(a.getBalance() > 0){
            
            return Response.status(401).entity("Cannot delete an account with an active balance.").build();
        }
        else{
            entityManager.getTransaction().begin();
            entityManager.remove(a);
            entityManager.getTransaction().commit();
            entityManager.close();
            return Response.status(200).entity("Deposit succsessful").build();
        }        
    }

    
    
    @POST
    @Path("{id}/deposit")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // curl -v -H "Accept: application/xml" -H "API_KEY:VALID_KEY" -H "Content-type: application/json" http://localhost:8080/api/users -d '{"name":"Dylan","age":22}'
    public Response deposit(@PathParam("id") int accountID, @QueryParam("amount") double amount) {

        entityManager.getTransaction().begin();
        
        //get account
        Account account = entityManager.find(Account.class, accountID);

        //create new transaction
        Transaction t = new Transaction();
        
        //set attributes
        t.setType("debit");
        t.setAmount(amount);
        t.setAccount(account);
        
        ArrayList<Transaction> list = new ArrayList<>();
        list.add(t);
        
        account.setTransaction(list);
        
        double balance = account.getBalance();
        double newbalance = balance+amount;
        account.setBalance(newbalance);

        entityManager.persist(account);
        
        entityManager.getTransaction().commit();
        
//        // retrive the user from the database
//        User test = entityManager.find(User.class, id);
//
//        // the retreived user has a filled arraylist of transfers which belong to this user
//        for (Account user1 : test.getAccounts()) {
//            System.out.println(user1);
//        }
        
        entityManager.close();
        return Response.status(201).entity("Deposit succsessful").build();
    }
    
    @POST
    @Path("{id}/withdraw")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // curl -v -H "Accept: application/xml" -H "API_KEY:VALID_KEY" -H "Content-type: application/json" http://localhost:8080/api/users -d '{"name":"Dylan","age":22}'
    public Response withdraw(@PathParam("id") int accountID, @QueryParam("amount") double amount) {
        
        Account account = entityManager.find(Account.class, accountID);
        
        if(account.getBalance() - amount > 0){
            
            entityManager.getTransaction().begin();
        
            //get account


            //create new transaction
            Transaction t = new Transaction();

            //set attributes
            t.setType("credit");
            t.setAmount(amount);
            t.setAccount(account);

            ArrayList<Transaction> list = new ArrayList<>();
            list.add(t);

            account.setTransaction(list);

            double balance = account.getBalance();
            double newbalance = balance-amount;
            account.setBalance(newbalance);
            
            entityManager.persist(account);

            entityManager.getTransaction().commit();

    //        // retrive the user from the database
    //        User test = entityManager.find(User.class, id);
    //
    //        // the retreived user has a filled arraylist of transfers which belong to this user
    //        for (Account user1 : test.getAccounts()) {
    //            System.out.println(user1);
    //        }

            entityManager.close();
            return Response.status(200).entity("Deposit succsessful").build();
        }
        else{
            return Response.status(200).entity("Insufficient Funds!!").build();
        }
        
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{id}/balance")
    public Response getBalance(@PathParam("id") int id) {
        Account a = entityManager.find(Account.class, id);
        String balance = Double.toString(a.getBalance());
        return Response.status(200).entity(balance).build();
    }
    
    @POST
    @Path("{AccNumber}/transfer")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // curl -v -H "Accept: application/xml" -H "API_KEY:VALID_KEY" -H "Content-type: application/json" http://localhost:8080/api/users -d '{"name":"Dylan","age":22}'
    public Response transfer(@PathParam("AccNumber") int AccNumber, @QueryParam("amount") double amount, @QueryParam("debitacc") int debitacc) {
       
        
        Account accountCredit = entityManager.createQuery("SELECT a FROM Account a where a.accountnumber = :accountnumber", Account.class)
                            .setParameter("accountnumber", AccNumber).getSingleResult();
        
        Account accountDebit = entityManager.createQuery("SELECT a FROM Account a where a.accountnumber = :accountnumber", Account.class)
                            .setParameter("accountnumber", debitacc).getSingleResult();
                            

        entityManager.getTransaction().begin();

        //create new transaction      
        Transaction tCredit = new Transaction();
        Transaction tDebit = new Transaction();

        //set attributes
        tCredit.setType("credit");
        tCredit.setAmount(amount);
        tCredit.setAccount(accountCredit);

        //set attributes
        tDebit.setType("debit");
        tDebit.setAmount(amount);
        tDebit.setAccount(accountDebit);
        
        //update balances
        double credBal = accountCredit.getBalance();
        double debBal = accountDebit.getBalance();
        
        accountCredit.setBalance(credBal - amount);
        accountDebit.setBalance(debBal + amount);

        ArrayList<Transaction> listCred = new ArrayList<>();
        listCred.add(tCredit);
        ArrayList<Transaction> listDeb = new ArrayList<>();
        listDeb.add(tDebit);

        accountCredit.setTransaction(listCred);
        accountDebit.setTransaction(listDeb);

       
        entityManager.persist(accountCredit);
        entityManager.persist(accountDebit);

        entityManager.getTransaction().commit();

        entityManager.close();
        return Response.status(200).entity("Deposit succsessful").build();

        
    }

}
