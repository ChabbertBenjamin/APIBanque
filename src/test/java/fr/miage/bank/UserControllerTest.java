package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.UserInput;
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
class UserControllerTest {

	@LocalServerPort
	int port;

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	public void setupContext(){
		RestAssured.port = port;
	}

	@Test
	public void getAllUsersTest(){

		User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
		userRepository.save(user1);
		Response response = when().get("users")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString, containsString("lastname"));
	}


	@Test
	public void getOneUserByIdTest(){
		User user1 = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
		userRepository.save(user1);
		User user2 = new User("2", "Holland", "Tom", new Date(), "Angleterre", "88888888","0606060606","tom@gmail.fr","4567");
		userRepository.save(user2);
		Response response = when().get("users/"+user1.getId())
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString, containsString("Peter"));
		assertThat(jsonAsString, Matchers.not(containsString("Tom")));

	}

	@Test
	public void saveUserTest() throws JsonProcessingException {
		UserInput user = new UserInput("Parker", "Peter", "peter@gmail.fr", "123456",  new Date(), "France", "123465789","0606060606");
		ObjectMapper map = new ObjectMapper();
		Response response = given()
				.body(map.writeValueAsString(user))
				.contentType(ContentType.JSON)
				.when()
				.post("/users")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();

		Response response1 = when().get("users")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response1.asString();
		assertThat(jsonAsString, containsString(user.getFirstname()));
	}

	@Test
	public void updateUserTest() throws JsonProcessingException {
		User user = new User("1", "Parker", "Peter", new Date(), "France", "12534534","0606060606","peter@gmail.fr","1234");
		userRepository.save(user);

		UserInput user1 = new UserInput("Parker", "Peter", "peter@gmail.fr", "123456",  new Date(), "Angleterre", "123465789","0606060606");
		ObjectMapper map = new ObjectMapper();
		Response response = given()
				.body(map.writeValueAsString(user1))
				.contentType(ContentType.JSON)
				.when()
				.put("users/"+user.getId())
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();

		Response response1 = when().get("users/"+user.getId())
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response1.asString();
		assertThat(jsonAsString, containsString("Angleterre"));
	}
}
