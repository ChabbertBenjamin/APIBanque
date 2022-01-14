package fr.miage.bank;

import fr.miage.bank.entity.*;
import fr.miage.bank.repository.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.sql.Timestamp;
import java.util.Date;

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
        Payment payment1 = new Payment("1",cart1,t1, 30, "France", account2);
        paymentRepository.save(payment1);
        Payment payment2 = new Payment("2",cart1,t2, 30, "Angleterre", account2);
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
        Payment payment1 = new Payment("1",cart1,t1, 30, "France", account2);
        paymentRepository.save(payment1);
        Payment payment2 = new Payment("2",cart1,t2, 30, "Angleterre", account2);
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
}
