package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.bank.entity.*;
import fr.miage.bank.input.PaymentInput;
import fr.miage.bank.input.UserInput;
import fr.miage.bank.repository.*;
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
import java.sql.Timestamp;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
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

    @Autowired
    PaymentRepository paymentRepository;

    @BeforeEach
    public void setupContext(){
        RestAssured.port = port;
    }

    @Test
    public void getAllPaiementsByCarteIdTest(){
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
        Payment payment1 = new Payment("1",cart1,t1, b, "France", account2);
        paymentRepository.save(payment1);
        Payment payment2 = new Payment("2",cart1,t2, b, "Angleterre", account2);
        paymentRepository.save(payment2);

        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN()+"/cartes/"+cart1.getId()+"/paiements")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("France"));
        assertThat(jsonAsString, containsString("Angleterre"));

    }


    @Test
    public void getOnePaiementByIdTest(){
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
        Payment payment1 = new Payment("1",cart1,t1, b, "France", account2);
        paymentRepository.save(payment1);
        Payment payment2 = new Payment("2",cart1,t2, b, "Angleterre", account2);
        paymentRepository.save(payment2);


        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN()+"/cartes/"+cart1.getId()+"/paiements/"+ payment1.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("France"));
        assertThat(jsonAsString, Matchers.not(containsString("Angleterre")));


    }

    @Test
    public void createPaiementTest() throws JsonProcessingException {
        User user = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user);

        Account account = new Account("546784651184","France","123",50.0,user);
        accountRepository.save(account);
        Account account1 = new Account("FR123456787","Angleterre","123",50.0,user);
        accountRepository.save(account1);

        Cart cart = new Cart("1","1234", "123", false, true, 500, true, false,null,"1234567891234567",account);
        cartRepository.save(cart);
        Timestamp t1 = new Timestamp(45664565L);
        BigDecimal b = new BigDecimal(10);
        PaymentInput payment = new PaymentInput(b,"Etats-Unis","FR123456787",cart,t1);
        ObjectMapper map = new ObjectMapper();
        Response response = given()
                .body(map.writeValueAsString(payment))
                .contentType(ContentType.JSON)
                .when()
                .post("/users/1/accounts/546784651184/cartes/1/paiements")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        Response response1 = when().get("/users/1/accounts/546784651184/cartes/1/paiements")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response1.asString();
        assertThat(jsonAsString, containsString(payment.getCountry()));
    }
}
