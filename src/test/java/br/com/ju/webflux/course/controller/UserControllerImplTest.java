package br.com.ju.webflux.course.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static reactor.core.publisher.Mono.just;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.mongodb.reactivestreams.client.MongoClient;

import br.com.ju.webflux.course.entity.User;
import br.com.ju.webflux.course.mapper.UserMapper;
import br.com.ju.webflux.course.model.request.UserRequest;
import br.com.ju.webflux.course.model.response.UserResponse;
import br.com.ju.webflux.course.service.UserService;
import br.com.ju.webflux.course.service.exception.ObjectNotFoundException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {
	
	private static final String PASSWORD = "123";

	private static final String EMAIL = "sara@mail.com";

	private static final String NAME = "Sara Mello";

	private static final String ID = "123456";

	@Autowired
	private WebTestClient webTestClient;
	
	@MockBean
	private UserService service;
	
	@MockBean
	private UserMapper mapper;
	
	@MockBean
	private MongoClient mongoClient;
		
	@Test
	@DisplayName("Test endpoint save and success")
	void testSaveWithSuccess() {
		final var request = new UserRequest(NAME, EMAIL, PASSWORD);
		
		when(service.save(any(UserRequest.class))).thenReturn(just(User.builder().build()));
		
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange().expectStatus().isCreated();
		
		verify(service).save(any(UserRequest.class));	
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for name with spaces at the beginning")
	void testSaveWithBadRequestForNameWithSpaces() {
		final var request = new UserRequest(NAME.concat(" "), EMAIL, PASSWORD);
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("name")
		.jsonPath("$.errors[0].message").isEqualTo("Field cannot have blank spaces at the beginning or at and");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for empty name")
	void testSaveWithBadRequestEmptyName() {
		final var request = new UserRequest(null , EMAIL, PASSWORD);
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("name")
		.jsonPath("$.errors[0].message").isEqualTo("must not be null or empty");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for name length minimum")
	void testSaveWithBadRequestNameLengthMin() {
		final var request = new UserRequest("An", EMAIL, PASSWORD);
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("name")
		.jsonPath("$.errors[0].message").isEqualTo("must be between 3 and 50 characters");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for name length maximum")
	void testSaveWithBadRequestNameLengthMax() {
		final var request = new UserRequest("Anjhytfdretgjgfrdswatuioplkjhgfdcbnhytfdeljhgfmjouytfdes", EMAIL, PASSWORD);
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("name")
		.jsonPath("$.errors[0].message").isEqualTo("must be between 3 and 50 characters");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request by invalid email")
	void testSaveWithBadRequestInvalidEmail() {
		final var request = new UserRequest(NAME, "sara.mail.com", PASSWORD);
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("email")
		.jsonPath("$.errors[0].message").isEqualTo("Invalid email");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for empty email")
	void testSaveWithBadRequestEmptyEmail() {
		final var request = new UserRequest(NAME, null, PASSWORD);
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("email")
		.jsonPath("$.errors[0].message").isEqualTo("must not be null or empty");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for password with spaces at the beginning")
	void testSaveWithBadRequestForPasswordWithSpaces() {
		final var request = new UserRequest(NAME, EMAIL, PASSWORD.concat(" "));
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("password")
		.jsonPath("$.errors[0].message").isEqualTo("Field cannot have blank spaces at the beginning or at and");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for empty password")
	void testSaveWithBadRequestEmptyPassword() {
		final var request = new UserRequest(NAME , EMAIL, null);
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("password")
		.jsonPath("$.errors[0].message").isEqualTo("must not be null or empty");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for password length minimum")
	void testSaveWithBadRequestPasswordLengthMin() {
		final var request = new UserRequest(NAME, EMAIL, "1");
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("password")
		.jsonPath("$.errors[0].message").isEqualTo("must be between 3 and 20 characters");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for password length maximum")
	void testSaveWithBadRequestPasswordLengthMax() {
		final var request = new UserRequest(NAME, EMAIL, "189546253147896524623589");
			
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Validation Error")
		.jsonPath("$.message").isEqualTo("Error on validation attributes")
		.jsonPath("$.errors[0].fieldName").isEqualTo("password")
		.jsonPath("$.errors[0].message").isEqualTo("must be between 3 and 20 characters");
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for duplicate email")
	void testSaveWithBadRequestForEmailDuplicate() {
		
		UserRequest request = new UserRequest(NAME, EMAIL, PASSWORD);
		when(service.save(any())).thenThrow(DuplicateKeyException.class);
		
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody().consumeWith(System.out::println)
		.jsonPath("$.path").isEqualTo("/users")
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Bad Request")
		.jsonPath("$.message").isEqualTo("E-mail already registered");
	}
	
	@Test
	@DisplayName("Test endpoint find by id with success")
	void testFindByIdWithSuccess() {
		final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
		
		when(service.findById(anyString())).thenReturn(just(User.builder().build()));
		when(mapper.toResponse(any(User.class))).thenReturn(userResponse);
		
		webTestClient.get().uri("/users/" + ID)
		.accept(APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo(ID)
		.jsonPath("$.name").isEqualTo(NAME)
		.jsonPath("$.email").isEqualTo(EMAIL)
		.jsonPath("$.password").isEqualTo(PASSWORD);
	}
	
	@Test
	@DisplayName("Test endpoint find by id with resource not found")
	void testFindByIdResourceNotFound() {
		
		when(service.findById(anyString())).thenThrow(ObjectNotFoundException.class);
		
		webTestClient.get().uri("/users/" + ID)
		.accept(APPLICATION_JSON)
		.exchange()
		.expectStatus().isNotFound()
		.expectBody()
		.jsonPath("$.status").isEqualTo(NOT_FOUND.value())
		.jsonPath("$.error").isEqualTo("Not Found");
	}
	
	@Test
	void testFindAll() {
	}
	
	@Test
	void testUpdate() {
	}
	
	@Test
	void testDelete() {
	}

}
