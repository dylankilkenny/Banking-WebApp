package com.example.jpa.services;

import com.example.jpa.models.API;
import com.example.jpa.models.Account;
import com.example.jpa.models.Transaction;
import com.example.jpa.models.User;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import org.hibernate.Criteria;

@Path("/users")
public class UserService {

    EntityManager entityManager;

    public UserService() {
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("test-connection");
        entityManager = emfactory.createEntityManager();
    }
    
    public boolean validateKey(int id, String key){
        User u = entityManager.find(User.class, id); 
        List<API> userKeys = u.getAPI();
        boolean valid = false;
        for(int i=0; i < userKeys.size(); i++){
            API api = userKeys.get(i);
            System.out.println(api.getApi_key());
            if (api.getApi_key().equals(key)){
                valid = true;
            }
        }
        return valid;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public User getUser(@PathParam("id") int id, @HeaderParam("API_TOKEN") String token) {
        System.out.println(token);
        if(validateKey(id, token)){
        
            
            
            User u = entityManager.find(User.class, id);       
            User r = new User();
            r.setAddress(u.getAddress());
            r.setName(u.getName());
            r.setAge(u.getAge());
            r.setEmail(u.getEmail());
            r.setId(u.getId());
            return r;
            
        }
        else{
            throw new NotFoundException("You are not authorized to access this.");
        }
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{id}/accounts")
    public List<Account> getAccounts(@PathParam("id") int id, @HeaderParam("API_TOKEN") String token) {
        if(validateKey(id, token)){
            User test = entityManager.find(User.class, id);
            List<Account> accounts = test.getAccounts();
            ArrayList<Account> r = new ArrayList<Account>();
            for(int i = 0; i < accounts.size(); i++){
            Account n = new Account();
            Account acc = accounts.get(i);
            n.setAccountnumber(acc.getAccountnumber());
            n.setBalance(acc.getBalance());
            n.setId(acc.getId());
            n.setSortcode(acc.getSortcode());
            n.setType(acc.getType());
            r.add(n);
        }
        return r;
            }
        else{
            throw new NotFoundException("You are not authorized to access this.");
        }
        
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/account/{accountID}")
    public Account getAccount(@PathParam("accountID") int accID) {
        Account acc = entityManager.find(Account.class, accID);
        return acc;
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{id}/transactions")
    public List<Transaction> getAllTransactions(@PathParam("id") int id, @HeaderParam("API_TOKEN") String token) {
        
        User u = entityManager.find(User.class, id);
        if(validateKey(id, token)){
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        List<Account> accounts = u.getAccounts();
        for(int i = 0; i < accounts.size(); i++){
            Account acc = accounts.get(i);
            List<Transaction> t = acc.getTransaction();
            for(int j = 0; j < t.size(); j++){
                Transaction trans = t.get(j);
                transactions.add(trans);         
            }
        } 
        return transactions;
        }
        else{
            throw new NotFoundException("You are not authorized to access this.");
        }
    }
    
    
    @DELETE
    @Path("{id}")
    // curl -v -X DELETE -H "API_KEY:VALID_KEY" http://localhost:8080/api/planets/1
    public Response deleteUser(@PathParam("id") int id, @HeaderParam("API_TOKEN") String token) {
        if(validateKey(id, token)){
        User u = entityManager.find(User.class, id);

        entityManager.getTransaction().begin();
        entityManager.remove(u);
        entityManager.getTransaction().commit();
        entityManager.close();

        return Response.status(Response.Status.ACCEPTED).build();
        }
        else{
            throw new NotFoundException("You are not authorized to access this.");
        }
    }
    
    @GET
    @Path("{id}/api")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // curl -v -X POST -H "API_KEY:VALID_KEY" "http://localhost:8080/api/users/1?name=kilkenny"
    public List<API> getAPI(@PathParam("id") int id, @QueryParam("pin") int pin, @HeaderParam("API_TOKEN") String token) throws NoSuchAlgorithmException {
        
        if(validateKey(id, token)){
        User user = entityManager.find(User.class, id);
        
        if (pin == user.getPin()){
            
            return user.getAPI();
            
            
        }
        else{
            throw new NotFoundException("Invalid pin!");
        }
        }
        else{
            throw new NotFoundException("You are not authorized to access this.");
        }
    }
    
    @POST
    @Path("login")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // curl -v -X POST -H "API_KEY:VALID_KEY" "http://localhost:8080/api/users/1?name=kilkenny"
    public User Login(@QueryParam("email") String email, @QueryParam("pin") int pin) throws NoSuchAlgorithmException {
        
        User u = entityManager.createQuery("SELECT a FROM User a where a.email = :email", User.class)
                            .setParameter("email", email).getSingleResult();

        if (pin == u.getPin()){
            
            
            return u;
        }
        else{
            throw new NotFoundException("Invalid pin!");
        }
    }

    @POST
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // curl -v -X POST -H "API_KEY:VALID_KEY" "http://localhost:8080/api/users/1?name=kilkenny"
    public Response update(@PathParam("id") int id, @HeaderParam("API_TOKEN") String token, @QueryParam("name") String name, @QueryParam("age") int age, @QueryParam("email") String email, @QueryParam("address") String address) {
        if(validateKey(id, token)){
            User user = entityManager.find(User.class, id);

            if (user == null){
                throw new NotFoundException("No user found :(");
            }

            entityManager.getTransaction().begin();
            if(name != null){
                user.setName(name);
            }
            if(age != 0){
                user.setAge(age);
            }
            if(address != null){
                user.setAddress(address);
            }
            if(email != null){
                user.setEmail(email);
            }
            entityManager.getTransaction().commit();

            return Response.status(200).entity("Success").build();
        }
        else{
            throw new NotFoundException("You are not authorized to access this.");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // curl -v -H "Accept: application/xml" -H "API_KEY:VALID_KEY" -H "Content-type: application/json" http://localhost:8080/api/users -d '{"name":"Dylan","age":22}'
    public Response CreateUser(User user) throws NoSuchAlgorithmException {

        entityManager.getTransaction().begin();

        
        Account acc = new Account();
        Random rand = new Random();
        int accountnumber = rand.nextInt(10000);

        acc.setAccountnumber(accountnumber);
        acc.setBalance(0);
        acc.setSortcode(9999);
        acc.setType("Current");
        acc.setUser(user);
        
        ArrayList<Account> list = new ArrayList<>();
        
        list.add(acc);
        
        user.setAccounts(list);
        
        API api = new API();
        String key = generate(128);
        api.setApi_key(key);
        api.setLevel("User");
        api.setLevelCode(1);
        api.setUser(user);

        ArrayList<API> apilist = new ArrayList<>();

        apilist.add(api);

        user.setAPI(apilist);


        entityManager.persist(acc);
        entityManager.persist(user);
        entityManager.getTransaction().commit();

        entityManager.close();

        return Response.status(200).entity(user).build();
    }
    
    @POST
    @Path("{id}/account")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // curl -v -H "Accept: application/xml" -H "API_KEY:VALID_KEY" -H "Content-type: application/json" http://localhost:8080/api/users -d '{"name":"Dylan","age":22}'
    public Response CreateAccount(@PathParam("id") int id, @HeaderParam("API_TOKEN") String token, @QueryParam("sortcode") int sortcode, @QueryParam("type") String type) {
        if(validateKey(id, token)){
            entityManager.getTransaction().begin();

            //Get the user
            User user = entityManager.find(User.class, id);

            //Create random account number
            Random rand = new Random();
            int accountnumber = rand.nextInt(100000);
            Account account = new Account();

            //set account attributes 
            account.setAccountnumber(accountnumber);
            account.setBalance(0);
            account.setSortcode(sortcode); 
            account.setType(type);
            account.setUser(user);

            //set the account to the user
            ArrayList<Account> list = new ArrayList<>();
            list.add(account);
            user.setAccounts(list);


            entityManager.persist(user);
            entityManager.getTransaction().commit();
    //        
    //        // retrive the user from the database
    //        User test = entityManager.find(User.class, id);
    //
    //        // the retreived user has a filled arraylist of transfers which belong to this user
    //        for (Account user1 : test.getAccounts()) {
    //            System.out.println(user1);
    //        }
    //        
            entityManager.close();
            return Response.status(200).entity("Account Created").build();
        }
        else{
            throw new NotFoundException("You are not authorized to access this.");
        }
    }
    
    // @reference https://stackoverflow.com/questions/28050977/generating-random-api-key-2-method-provided-any-difference
    public static String generate(final int keyLen) throws NoSuchAlgorithmException{

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keyLen);
        SecretKey secretKey = keyGen.generateKey();
        byte[] encoded = secretKey.getEncoded();
        return DatatypeConverter.printHexBinary(encoded).toLowerCase();
    }
    
    
    
    
    

    
  

}
