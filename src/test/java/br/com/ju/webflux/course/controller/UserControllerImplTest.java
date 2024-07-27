package br.com.ju.webflux.course.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static reactor.core.publisher.Mono.just;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.mongodb.reactivestreams.client.MongoClient;

import br.com.ju.webflux.course.entity.User;
import br.com.ju.webflux.course.mapper.UserMapper;
import br.com.ju.webflux.course.model.request.UserRequest;
import br.com.ju.webflux.course.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {
	
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
		final var request = new UserRequest("Sara Mello", "sara@mail.com", "123");
		
		when(service.save(any(UserRequest.class))).thenReturn(just(User.builder().build()));
		
		webTestClient.post().uri("/users")
		.contentType(APPLICATION_JSON)
		.body(fromValue(request))
		.exchange().expectStatus().isCreated();
		
		verify(service).save(any(UserRequest.class));	
	}
	
	@Test
	@DisplayName("Test endpoint save with bad request")
	void testSaveWithBadRequest() {
		final var request = new UserRequest(" Sara Mello", "sara@mail.com", "123");
			
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
	void testFindById() {
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
