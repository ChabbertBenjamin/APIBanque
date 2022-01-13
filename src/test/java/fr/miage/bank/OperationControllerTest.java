package fr.miage.bank;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.entity.User;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import fr.miage.bank.repository.OperationRepository;
import fr.miage.bank.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

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

        Cart cart1 = new Cart("1","1234", "123", false, false, 500, true, false,account1);
        cartRepository.save(cart1);

        Cart cart2 = new Cart("2","4567", "456", false, false, 500, true, false,account2);
        cartRepository.save(cart2);

        Timestamp t1 = new Timestamp(45664565L);
        Timestamp t2 = new Timestamp(456648876554L);
        Operation operation1 = new Operation("1",t1, "payement", 30, 1, account1,account2 ,"Benjamin", "Noel", "France",cart1);
        operationRepository.save(operation1);
        Operation operation2 = new Operation("2",t2, "cadeau", 30, 1, account1,account2 ,"Benjamin", "Noel", "France",cart1);
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
    public void getOneAccountByIdTest(){
        User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user1);

        Account account1 = new Account("FR123456789","France","123",50.0,user1);
        accountRepository.save(account1);

        Account account2 = new Account("FR987654321","France","123",50.0,user1);
        accountRepository.save(account2);

        Cart cart1 = new Cart("1","1234", "123", false, false, 500, true, false,account1);
        cartRepository.save(cart1);


        Timestamp t1 = new Timestamp(45664565L);

        Operation operation1 = new Operation("1",t1, "payement", 30, 1, account1,account2 ,"Benjamin", "Noel", "France",cart1);
        operationRepository.save(operation1);


        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN()+"/operations/"+operation1.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString(operation1.getText()));

    }


}
