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
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

	@MockBean
	private UserMapper mapper;
	
	@MockBean
	private UserService service;
	
	@MockBean
	private MongoClient mongoClient;
	
	@Autowired
	private WebTestClient webTestClient;
	
	private static final String ID = "123456";
	private static final String URI = "/users";
	private static final String PASSWORD = "123";
	private static final String NAME = "Sara Mello";
	private static final String EMAIL = "sara@mail.com";
		
	@Test
	@DisplayName("Test endpoint save and success")
	void testSaveWithSuccess() {
		final var request = new UserRequest(NAME, EMAIL, PASSWORD);
		
		when(service.save(any(UserRequest.class))).thenReturn(just(User.builder().build()));
		
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange().expectStatus().isCreated();
		
		verify(service).save(any(UserRequest.class));	
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request for name with spaces at the beginning")
	void testSaveWithBadRequestForNameWithSpaces() {
		final var request = new UserRequest(NAME.concat(" "), EMAIL, PASSWORD);
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
			
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.path").isEqualTo(URI)
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
		
		webTestClient.post().uri(URI)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody().consumeWith(System.out::println)
		.jsonPath("$.path").isEqualTo(URI)
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Bad Request")
		.jsonPath("$.message").isEqualTo("E-mail already registered");
		
		verify(service).save(any(UserRequest.class));	
	}
	
	@Test
	@DisplayName("Test endpoint find by id with success")
	void testFindByIdWithSuccess() {
		final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
		
		when(service.findById(anyString())).thenReturn(just(User.builder().build()));
		when(mapper.toResponse(any(User.class))).thenReturn(userResponse);
		
		webTestClient.get().uri(URI + "/" + ID)
		.accept(APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo(ID)
		.jsonPath("$.name").isEqualTo(NAME)
		.jsonPath("$.email").isEqualTo(EMAIL)
		.jsonPath("$.password").isEqualTo(PASSWORD);
		
		verify(service).findById(anyString());
		verify(mapper).toResponse(any(User.class));
	}
	
	@Test
	@DisplayName("Test endpoint find by id with resource not found")
	void testFindByIdResourceNotFound() {
		
		when(service.findById(anyString())).thenThrow(ObjectNotFoundException.class);
		
		webTestClient.get().uri(URI + "/"  + ID)
		.accept(APPLICATION_JSON)
		.exchange()
		.expectStatus().isNotFound()
		.expectBody()
		.jsonPath("$.status").isEqualTo(NOT_FOUND.value())
		.jsonPath("$.error").isEqualTo("Not Found");
		
		verify(service).findById(anyString());
	}
	
	@Test
	@DisplayName("Test endpoint find all with success")
	void testFindAllWithSuccess() {
		
		final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
		
		when(service.findAll()).thenReturn(Flux.just(User.builder().build()));
		when(mapper.toResponse(any(User.class))).thenReturn(userResponse);
		
		webTestClient.get().uri(URI)
		.accept(APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.[0].id").isEqualTo(ID)
		.jsonPath("$.[0].name").isEqualTo(NAME)
		.jsonPath("$.[0].email").isEqualTo(EMAIL)
		.jsonPath("$.[0].password").isEqualTo(PASSWORD);
		
		verify(service).findAll();	
		verify(mapper).toResponse(any(User.class));
	}
	
	@Test
	@DisplayName("Test endpoint update with success")
	void testUpdateWithSuccess() {
		
		final var request = new UserRequest(NAME, EMAIL, PASSWORD);
		final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
		
		when(service.update(anyString(), any(UserRequest.class))).thenReturn(just(User.builder().build()));
		when(mapper.toResponse(any(User.class))).thenReturn(userResponse);
		
		webTestClient.patch().uri(URI + "/" + ID)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo(ID)
		.jsonPath("$.name").isEqualTo(NAME)
		.jsonPath("$.email").isEqualTo(EMAIL)
		.jsonPath("$.password").isEqualTo(PASSWORD);
		
		verify(service).update(anyString(), any(UserRequest.class));	
		verify(mapper).toResponse(any(User.class));
	}
	
	@Test
	@DisplayName("Test endpoint update resource not found")
	void testUpdateResourceNotFound() {
		
		final var request = new UserRequest(NAME, EMAIL, PASSWORD);
		final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
		
		when(service.update(anyString(), any(UserRequest.class))).thenThrow(ObjectNotFoundException.class);
		when(mapper.toResponse(any(User.class))).thenReturn(userResponse);
		
		webTestClient.patch().uri(URI + "/" + ID)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isNotFound()
		.expectBody().consumeWith(System.out::println)
		.jsonPath("$.path").isEqualTo(URI + "/" + ID)
		.jsonPath("$.status").isEqualTo(NOT_FOUND.value())
		.jsonPath("$.error").isEqualTo("Not Found");
		
		verify(service).update(anyString(), any(UserRequest.class));	
	}
	
	@Test
	@DisplayName("Test endpoint update resource not found for email duplicate")
	void testUpdateResourceNotFoundForEmailDuplicate() {
		
		final var request = new UserRequest(NAME, EMAIL, PASSWORD);
		
		when(service.update(anyString(), any(UserRequest.class))).thenThrow(DuplicateKeyException.class);
		
		webTestClient.patch().uri(URI + "/" + ID)
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody().consumeWith(System.out::println)
		.jsonPath("$.path").isEqualTo(URI + "/" + ID)
		.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
		.jsonPath("$.error").isEqualTo("Bad Request")
		.jsonPath("$.message").isEqualTo("E-mail already registered");
		
		verify(service).update(anyString(), any(UserRequest.class));	
	}
	
	@Test
	@DisplayName("Test endpoint delete with success")
	void testDeleteWithSuccess() {
		
		when(service.delete(anyString())).thenReturn(just(User.builder().build()));
		
		webTestClient.delete().uri(URI + "/" + ID)
		.exchange()
		.expectStatus().isOk();
		
		verify(service).delete(anyString());	
	}
	
	@Test
	@DisplayName("Test endpoint delete with resource not found")
	void testDeleteWithResourceNotFound() {
		
		when(service.delete(anyString())).thenThrow(ObjectNotFoundException.class);
		
		webTestClient.delete().uri(URI + "/" + ID)
		.exchange()
		.expectStatus().isNotFound()
		.expectBody().consumeWith(System.out::println)
		.jsonPath("$.path").isEqualTo(URI + "/" + ID)
		.jsonPath("$.status").isEqualTo(NOT_FOUND.value())
		.jsonPath("$.error").isEqualTo("Not Found");
		
		verify(service).delete(anyString());	
	}

}
