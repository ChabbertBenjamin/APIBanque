package fr.miage.bank;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.User;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
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
public class CartControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CartRepository cartRepository;

    @BeforeEach
    public void setupContext(){
        RestAssured.port = port;
    }

    @Test
    public void getOneCartByIdAndAccountId(){
        User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user1);

        Account account1 = new Account("546784651184","France","123",50.0,user1);
        accountRepository.save(account1);

        Cart cart1 = new Cart("1","1234", "123", false, false, 500, true, false,null,"1234567891234567",account1);
        cartRepository.save(cart1);

        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN()+"/cartes/"+cart1.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString(cart1.getCrypto()));

    }

    @Test
    public void getAllCartsByAccountIdTest(){
        User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
        userRepository.save(user1);

        Account account1 = new Account("546784651184","France","123",50.0,user1);
        accountRepository.save(account1);

        Cart cart1 = new Cart("1","1234", "123", false, false, 500, true, false,null,"1234567891234567",account1);
        cartRepository.save(cart1);

        Cart cart2= new Cart("2","0000", "456", false, false, 500, true, false,null,"1234567891234567",account1);
        cartRepository.save(cart2);

        Response response = when().get("users/"+user1.getId()+"/accounts/"+account1.getIBAN()+"/cartes")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString(cart1.getCrypto()));
        assertThat(jsonAsString, containsString(cart2.getCrypto()));

    }


}
