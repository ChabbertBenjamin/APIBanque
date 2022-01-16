package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.AccountInput;
import fr.miage.bank.input.UserInput;
import fr.miage.bank.repository.AccountRepository;
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

import java.util.Date;

import static io.restassured.RestAssured.given;
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

        Account account2 = new Account("FR123456788","France","456",50.0,user1);
        accountRepository.save(account2);

        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("FR123456789"));
        assertThat(jsonAsString, Matchers.not(containsString("FR123456788")));

    }

    @Test
    public void saveAccountTest() throws JsonProcessingException {
        //UserInput user = new UserInput("Parker", "Peter", "peter@gmail.fr", "123456",  new Date(), "France", "123465789","0606060606");
        User user = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user);
        AccountInput account = new AccountInput("France","12345",50.0,user);

        ObjectMapper map = new ObjectMapper();
        Response response = given()
                .body(map.writeValueAsString(account))
                .contentType(ContentType.JSON)
                .when()
                .post("/users/1/accounts")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        Response response1 = when().get("/users/1/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response1.asString();
        assertThat(jsonAsString, containsString(account.getSecret()));
    }

    @Test
    public void updateAccountTest() throws JsonProcessingException {
        User user = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user);

        Account account1 = new Account("FR123456789","France","12345",50.0,user);
        accountRepository.save(account1);
        AccountInput account = new AccountInput("Angleterre","12345",50.0,user);
        ObjectMapper map = new ObjectMapper();
        Response response = given()
                .body(map.writeValueAsString(account))
                .contentType(ContentType.JSON)
                .when()
                .put("users/1/accounts/FR123456789")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        Response response1 = when().get("users/1/accounts/"+account1.getIBAN())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response1.asString();
        assertThat(jsonAsString, containsString("Angleterre"));
    }


}
