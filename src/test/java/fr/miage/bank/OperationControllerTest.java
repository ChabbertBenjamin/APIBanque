package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.OperationInput;
import fr.miage.bank.input.PaymentInput;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import fr.miage.bank.repository.OperationRepository;
import fr.miage.bank.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OperationControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OperationRepository operationRepository;

    @BeforeEach
    public void setupContext(){
        RestAssured.port = port;
    }

    @Test
    public void getAllOperationsByAccountIdTest(){
        User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user1);

        Account account1 = new Account("FR123456789","France","123",50.0,user1);
        accountRepository.save(account1);

        Account account2 = new Account("FR987654321","France","123",50.0,user1);
        accountRepository.save(account2);

        Cart cart1 = new Cart("1","1234", "123", false, false, 500, true, false,null,"1234567891234567",account1);
        cartRepository.save(cart1);

        Cart cart2 = new Cart("2","4567", "456", false, false, 500, true, false,null,"1234567891234568",account2);
        cartRepository.save(cart2);

        Timestamp t1 = new Timestamp(45664565L);
        Timestamp t2 = new Timestamp(456648876554L);
        BigDecimal b = new BigDecimal(30);
        Operation operation1 = new Operation("1",t1, "payement", b, 1, account1,account2 , "Noel", "France");
        operationRepository.save(operation1);
        Operation operation2 = new Operation("2",t2, "cadeau", b, 1, account1,account2 , "Noel", "France");
        operationRepository.save(operation2);

        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN()+"/operations")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString(operation1.getText()));
        assertThat(jsonAsString, containsString(operation2.getText()));

    }


    @Test
    public void getOneOperationByIdTest(){
        User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user1);

        Account account1 = new Account("FR123456789","France","123",50.0,user1);
        accountRepository.save(account1);

        Account account2 = new Account("FR987654321","France","123",50.0,user1);
        accountRepository.save(account2);

        Cart cart1 = new Cart("1","1234", "123", false, false, 500, true, false,null,"1234567891234567",account1);
        cartRepository.save(cart1);


        Timestamp t1 = new Timestamp(45664565L);
        Timestamp t2 = new Timestamp(8976218646L);
        BigDecimal b = new BigDecimal(30);
        Operation operation1 = new Operation("1",t1, "payement", b, 1, account1,account2 , "Noel", "France");
        operationRepository.save(operation1);

        Operation operation2 = new Operation("2",t2, "cadeau", b, 1, account1,account2 , "Noel", "France");
        operationRepository.save(operation2);


        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN()+"/operations/"+operation1.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("payement"));
        assertThat(jsonAsString, Matchers.not(containsString("cadeau")));

    }

    @Test
    public void getAllOperationsByAccountIdAndCategory(){
        User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user1);

        Account account1 = new Account("FR123456789","France","123",50.0,user1);
        accountRepository.save(account1);

        Account account2 = new Account("FR987654321","France","123",50.0,user1);
        accountRepository.save(account2);

        Cart cart1 = new Cart("1","1234", "123", false, false, 500, true, false,null,"1234567891234567",account1);
        cartRepository.save(cart1);

        Cart cart2 = new Cart("2","4567", "456", false, false, 500, true, false,null,"1234567891234568",account2);
        cartRepository.save(cart2);

        Timestamp t1 = new Timestamp(45664565L);
        Timestamp t2 = new Timestamp(456648876554L);
        BigDecimal b = new BigDecimal(30);
        Operation operation1 = new Operation("1",t1, "payement", b, 1, account1,account2 , "achat", "France");
        operationRepository.save(operation1);
        Operation operation2 = new Operation("2",t2, "cadeau", b, 1, account1,account2 , "Noel", "France");
        operationRepository.save(operation2);

        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN()+"/operations/categorie/"+operation1.getCategory())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("payement"));
        assertThat(jsonAsString, Matchers.not(containsString("cadeau")));

    }

    @Test
    public void createOperationTest() throws JsonProcessingException {
        User user = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user);

        User user1 = new User("2", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user1);

        Account account = new Account("FR25553544554546","France","120453",50.0,user);
        accountRepository.save(account);
        Account account1 = new Account("ENG123456787","Angleterre","12345",50.0,user1);
        accountRepository.save(account1);

        Timestamp t1 = new Timestamp(45664565L);
        BigDecimal b = new BigDecimal(10);
        OperationInput operation = new OperationInput(t1,"cadeau noel",b,1,account1,account,"cadeau","Angleterre");
        ObjectMapper map = new ObjectMapper();
        Response response = given()
                .body(map.writeValueAsString(operation))
                .contentType(ContentType.JSON)
                .when()
                .post("/users/1/accounts/FR25553544554546/operations")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        Response response1 = when().get("/users/1/accounts/FR25553544554546/operations")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response1.asString();
        assertThat(jsonAsString, containsString(operation.getCountry()));
    }


}
