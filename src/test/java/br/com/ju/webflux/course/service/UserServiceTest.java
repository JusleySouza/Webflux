package br.com.ju.webflux.course.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ju.webflux.course.entity.User;
import br.com.ju.webflux.course.mapper.UserMapper;
import br.com.ju.webflux.course.model.request.UserRequest;
import br.com.ju.webflux.course.repository.UserRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	@Mock
	private UserRepository repository;
	@Mock
	private UserMapper mapper;

	@InjectMocks
	private UserService service;

	@Test
	void save() {
		UserRequest request = new UserRequest("Sara Mello", "sara@mail.com", "123");
		User entity = User.builder().build();
		
		when(mapper.toEntity(any(UserRequest.class))).thenReturn(entity);
		when(repository.save(any(User.class))).thenReturn(Mono.just(User.builder().build()));
		
		Mono<User> result = service.save(request);
		StepVerifier.create(result)
		.expectNextMatches(Objects::nonNull)
		.expectComplete()
		.verify();
	}

}
