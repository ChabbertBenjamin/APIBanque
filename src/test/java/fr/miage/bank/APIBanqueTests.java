package fr.miage.bank;

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

import java.util.Date;

import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class APIBanqueTests {

	@LocalServerPort
	int port;

	@BeforeEach
	public void setupContext(){
		RestAssured.port = port;
	}

	@Test
	public void getAllUsersTest(){
/*
		User user1 = new User("5", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
		ur.save(user1);
		System.out.println(user1.getId());
		System.out.println(user1.getLastname());
*/
		Response response = when().get("users")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString, containsString("lastname"));
	}


}
