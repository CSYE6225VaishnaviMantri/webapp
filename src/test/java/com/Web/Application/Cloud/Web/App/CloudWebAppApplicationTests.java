package com.Web.Application.Cloud.Web.App;

import com.Web.Application.Cloud.Web.App.entity.User;
import com.Web.Application.Cloud.Web.App.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CloudWebAppApplicationTests {

	@Test
	void contextLoads() {
	}

	private static ConfigurableApplicationContext appContext;

	@Autowired
	private UserRepository userRepository;

	@BeforeAll
	public static void setUp() {
		RestAssured.baseURI = "http://localhost:8080";
		appContext = SpringApplication.run(CloudWebAppApplication.class);
	}

	@AfterAll
	public static void tearDown() {
		appContext.close();
	}

	@Test
	@Order(1)
	void CreationAndValidationOfAccount() throws Exception {

		User newUser = new User();
		newUser.setUsername("tanya@gmail.com");
		newUser.setPassword("StrongPassword123");
		newUser.setFirst_name("Tanya");
		newUser.setLast_name("Shetty");


		User existingUser = userRepository.findByUsername("tanya@gmail.com");
		if (existingUser != null) {
			given()
					.contentType(ContentType.JSON)
					.body(newUser)
					.when()
					.post("/v1/user")
					.then()
					.assertThat()
					.statusCode(HttpStatus.BAD_REQUEST.value());

			return;
		}


		given()
				.contentType(ContentType.JSON)
				.body(newUser)
				.when()
				.post("/v1/user")
				.then()
				.assertThat()
				.statusCode(HttpStatus.CREATED.value());


		given()
				.header(HttpHeaders.AUTHORIZATION, "Basic " + getBase64Credentials("tanya@gmail.com", "StrongPassword123"))
				.when()
				.get("/v1/user/self")
				.then()
				.assertThat()
				.statusCode(HttpStatus.OK.value())
				.body("username", equalTo("tanya@gmail.com"))
				.body("first_name", equalTo("Tanya"))
				.body("last_name", equalTo("Shetty"));


		given()
				.contentType(ContentType.JSON)
				.body(newUser)
				.when()
				.post("/v1/user")
				.then()
				.assertThat()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}


	@Test
	void UserCreationUsingIncorrectEmail() {
			User newUser = new User();
			newUser.setUsername("InvalidEmail");
			given()
					.contentType(ContentType.JSON)
					.body(newUser)
					.when()
					.post("/v1/user")
					.then()
					.assertThat()
					.statusCode(HttpStatus.BAD_REQUEST.value());

	}


	@Test
	void UserCreationUsingIncorrectPassword() {
			User newUser = new User();
			newUser.setUsername("Cloud@test.com");
			newUser.setPassword("weak");

			given()
					.contentType(ContentType.JSON)
					.body(newUser)
					.when()
					.post("/v1/user")
					.then()
					.assertThat()
					.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void UserCreationUsingIncorrectFirstName() {
			User newUser = new User();
			newUser.setUsername("Cloud@test.com");
			newUser.setPassword("StrongPassword123");
			newUser.setFirst_name("");
			newUser.setLast_name("Doe");

			given()
					.contentType(ContentType.JSON)
					.body(newUser)
					.when()
					.post("/v1/user")
					.then()
					.assertThat()
					.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void UserCreationUsingIncorrectLastName() {
			User newUser = new User();
			newUser.setUsername("Cloud@test.com");
			newUser.setPassword("StrongPassword123");
			newUser.setFirst_name("Test");
			newUser.setLast_name("");

			given()
					.contentType(ContentType.JSON)
					.body(newUser)
					.when()
					.post("/v1/user")
					.then()
					.assertThat()
					.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void UserCreationUsingMissingFields() {
		User newUser = new User();
			given()
					.contentType(ContentType.JSON)
					.body(newUser)
					.when()
					.post("/v1/user")
					.then()
					.assertThat()
					.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@Order(2)
	void UpdatingAndValidatingUser() {
		String username = "tanya@gmail.com";
		String password = "StrongPassword123";
		String credentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

		User existingUser = userRepository.findByUsername(username);

		if (existingUser != null) {

			User updatedUser = new User();
			updatedUser.setUsername(existingUser.getUsername()); // Preserve username
			updatedUser.setFirst_name("UpdatedFirstName");
			updatedUser.setLast_name("UpdatedLastName");
			updatedUser.setPassword("NewStrongPassword123");
			System.out.println("Request Body: " + updatedUser.toString());


			given()
					.header("Authorization", "Basic " + credentials)
					.contentType(ContentType.JSON)
					.body(updatedUser)
					.when()
					.put("/v1/user/self")
					.then()
					.log().all()
					.assertThat()
					.statusCode(HttpStatus.NO_CONTENT.value());


			User fetchedUser = userRepository.findByUsername(username);


			assertEquals("UpdatedFirstName", fetchedUser.getFirst_name());
			assertEquals("UpdatedLastName", fetchedUser.getLast_name());
			assertEquals(existingUser.getUsername(), fetchedUser.getUsername());
			assertEquals(existingUser.getAccount_created(), fetchedUser.getAccount_created());
			assertNotEquals(existingUser.getAccount_updated(), fetchedUser.getAccount_updated());

			given()
					.header(HttpHeaders.AUTHORIZATION, "Basic " + getBase64Credentials("tanya@gmail.com", "NewStrongPassword123"))
					.when()
					.get("/v1/user/self")
					.then()
					.assertThat()
					.statusCode(HttpStatus.OK.value())
					.body("username", equalTo("tanya@gmail.com"))
					.body("first_name", equalTo("UpdatedFirstName"))
					.body("last_name", equalTo("UpdatedLastName"));



		} else {
			fail("User does not exist for username: " + username);
		}
	}

	@Test
	@Order(3)
	void UpdatingUserWithBlankFirstName() {
		String username = "tanya@gmail.com";
		String password = "NewStrongPassword123";
		String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

		String requestBody = "{\"username\":\"tanya@gmail.com\", \"first_name\":\"\", \"last_name\":\"UpdatedLastName\"}";

		given()
				.contentType(ContentType.JSON)
				.header("Authorization", "Bearer " + token)
				.body(requestBody)
				.when()
				.put("/v1/user/self")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.body(equalTo("First Name Field Cannot be Empty"));
	}

	@Test
	@Order(3)
	void UpdatingUserWithBlankLastName() {
		String username = "tanya@gmail.com";
		String password = "NewStrongPassword123";
		String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

		String requestBody = "{\"username\":\"tanya@gmail.com\",\"first_name\":\"UpdatedFirstName\", \"last_name\":\"\"}";

		given()
				.contentType(ContentType.JSON)
				.header("Authorization", "Bearer " + token)
				.body(requestBody)
				.when()
				.put("/v1/user/self")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.body(equalTo("Last Name Field Cannot be Empty"));
	}




	private String getBase64Credentials(String username, String password) {
		String credentials = username + ":" + password;
		byte[] credentialsBytes = credentials.getBytes(StandardCharsets.UTF_8);
		return Base64.getEncoder().encodeToString(credentialsBytes);
	}
}
