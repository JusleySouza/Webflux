package br.com.ju.webflux.course.service;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ju.webflux.course.entity.User;
import br.com.ju.webflux.course.mapper.UserMapper;
import br.com.ju.webflux.course.model.request.UserRequest;
import br.com.ju.webflux.course.repository.UserRepository;
import br.com.ju.webflux.course.service.exception.ObjectNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserMapper mapper;

	@InjectMocks
	private UserService service;
	
	@Mock
	private UserRepository repository;
	
	private static final String ID = "12345";
	private static final String PASSWORD = "123";
	private static final String NAME = "Sara Mello";
	private static final String EMAIL = "sara@mail.com";

	@Test
	void testSave() {
		UserRequest request = new UserRequest(NAME, EMAIL, PASSWORD);
		User entity = User.builder().build();
		
		when(mapper.toEntity(any(UserRequest.class))).thenReturn(entity);
		when(repository.save(any(User.class))).thenReturn(Mono.just(User.builder().build()));
		
		Mono<User> result = service.save(request);
		
		StepVerifier.create(result)
		.expectNextMatches(Objects::nonNull)
		.expectComplete()
		.verify();
		
		Mockito.verify(repository, times(1)).save(any(User.class));
	}
	
	@Test
	void testFindById() {
		when(repository.findById(anyString())).thenReturn(Mono.just(User.builder().build()));
		
		Mono<User> result = service.findById(ID);
		
		StepVerifier.create(result)
		.expectNextMatches(Objects::nonNull)
		.expectComplete()
		.verify();
		
		Mockito.verify(repository, times(1)).findById(anyString());
	}
	
	@Test
	void testFindAll() {
		when(repository.findAll()).thenReturn(Flux.just(User.builder().build()));
		
		Flux<User> result = service.findAll();
				
		StepVerifier.create(result)
		.expectNextMatches(Objects::nonNull)
		.expectComplete()
		.verify();
		
		Mockito.verify(repository, times(1)).findAll();
	}
	
	@Test
	void testUpdate() {
		UserRequest request = new UserRequest(NAME, EMAIL, PASSWORD);
		User entity = User.builder().build();
		
		when(mapper.toEntity(any(UserRequest.class), any(User.class))).thenReturn(entity);
		when(repository.findById(anyString())).thenReturn(Mono.just(entity));
		when(repository.save(any(User.class))).thenReturn(Mono.just(entity));
		
		Mono<User> result = service.update(ID, request);
		
		StepVerifier.create(result)
		.expectNextMatches(Objects::nonNull)
		.expectComplete()
		.verify();
		
		Mockito.verify(repository, times(1)).save(any(User.class));
	}
	
	@Test
	void testDelete() {
		User entity = User.builder().build();
		when(repository.findAndRemove(anyString())).thenReturn(Mono.just(entity));
		
		Mono<User> result = service.delete(ID);
		
		StepVerifier.create(result)
		.expectNextMatches(Objects::nonNull)
		.expectComplete()
		.verify();
		
		Mockito.verify(repository, times(1)).findAndRemove(anyString());
	}
	
	@Test
	void testHandleNotFound() {
		when(repository.findById(anyString())).thenReturn(Mono.empty());
		
		try {
			service.findById(ID).block();
		} catch (Exception ex) {
			assertEquals(ObjectNotFoundException.class, ex.getClass());
			assertEquals(format("Object not found. Id: %s, Type: %s ", ID, User.class.getSimpleName()),
					ex.getMessage());
		}
	}

}
