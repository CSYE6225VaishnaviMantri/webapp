package com.Web.Application.Cloud.Web.App;

import com.Web.Application.Cloud.Web.App.entity.User;
import com.Web.Application.Cloud.Web.App.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CloudWebAppApplicationTests {

	private static final String USERNAME = "tanya@gmail.com";
	private static final String PASSWORD = "StrongPassword123";
	private static final String NEW_PASSWORD = "NewStrongPassword123";
	private static final String UPDATED_FIRST_NAME = "UpdatedFirstName";
	private static final String UPDATED_LAST_NAME = "UpdatedLastName";
	private static final String AUTHORIZATION_HEADER = "Basic " +
			Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes());
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
		newUser.setIs_verified(0); // Set is_verified to true
		newUser.setVerification_expiration(LocalDateTime.now().plusMinutes(5)); // Set verification_expiration to 5 minutes from now



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
				.statusCode(HttpStatus.FORBIDDEN.value());


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

		User updatedUser = new User();
		updatedUser.setFirst_name(UPDATED_FIRST_NAME);
		updatedUser.setLast_name(UPDATED_LAST_NAME);
		updatedUser.setPassword(NEW_PASSWORD);


		System.out.println("Request Body: " + updatedUser.toString());
		System.out.println("Authorization Header: " + AUTHORIZATION_HEADER);

		given()
				.header("Authorization", AUTHORIZATION_HEADER)
				.contentType(ContentType.JSON)
				.body(updatedUser)
				.when()
				.put("/v1/user/self")
				.then()
				.log().all() // Log response details for debugging
				.assertThat()
				.statusCode(HttpStatus.FORBIDDEN.value());

		User fetchedUser = userRepository.findByUsername(USERNAME);
		assertEquals("Tanya", fetchedUser.getFirst_name());
		assertEquals("Shetty", fetchedUser.getLast_name());


		given()
				.header(HttpHeaders.AUTHORIZATION, "Basic " + getBase64Credentials("tanya@gmail.com", "NewStrongPassword123"))
				.when()
				.get("/v1/user/self")
				.then()
				.assertThat()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}

	@Test
	@Order(3)
	void UpdatingUserWithBlankFirstName() {
		String username = "tanya@gmail.com";
		String password = "NewStrongPassword123";
		String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

		String requestBody = "{\"first_name\":\"\", \"last_name\":\"UpdatedLastName\"}";

		given()
				.contentType(ContentType.JSON)
				.header("Authorization", "Bearer " + token)
				.body(requestBody)
				.when()
				.put("/v1/user/self")
				.then()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}

	@Test
	@Order(3)
	void UpdatingUserWithBlankLastName() {
		String username = "tanya@gmail.com";
		String password = "NewStrongPassword123";
		String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

		String requestBody = "{\"first_name\":\"UpdatedFirstName\", \"last_name\":\"\"}";

		given()
				.contentType(ContentType.JSON)
				.header("Authorization", "Bearer " + token)
				.body(requestBody)
				.when()
				.put("/v1/user/self")
				.then()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}




	private String getBase64Credentials(String username, String password) {
		String credentials = username + ":" + password;
		byte[] credentialsBytes = credentials.getBytes(StandardCharsets.UTF_8);
		return Base64.getEncoder().encodeToString(credentialsBytes);
	}
}
