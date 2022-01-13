package fr.miage.bank;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.User;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Date;

import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    public void setupContext(){
        RestAssured.port = port;
    }

    @Test
    public void getAllAccountsByUserIdTest(){
        User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user1);

        Account account1 = new Account("FR123456789","France","123",50.0,user1);
        accountRepository.save(account1);

        Account account2 = new Account("FR987654321","France","123",50.0,user1);
        accountRepository.save(account2);
        Response response = when().get("users/"+user1.getId()+"/accounts/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString(account1.getIBAN()));
        assertThat(jsonAsString, containsString(account2.getIBAN()));

    }


    @Test
    public void getOneAccountByIdTest(){
        User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user1);

        Account account1 = new Account("FR123456789","France","123",50.0,user1);
        accountRepository.save(account1);
        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString(account1.getIBAN()));

    }


}
