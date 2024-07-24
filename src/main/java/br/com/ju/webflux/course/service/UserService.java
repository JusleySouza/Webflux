package br.com.ju.webflux.course.service;

import org.springframework.stereotype.Service;
import static java.lang.String.format;
import br.com.ju.webflux.course.entity.User;
import br.com.ju.webflux.course.mapper.UserMapper;
import br.com.ju.webflux.course.model.request.UserRequest;
import br.com.ju.webflux.course.repository.UserRepository;
import br.com.ju.webflux.course.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository repository;
	private final UserMapper mapper;
	
	public Mono<User> save(final UserRequest request){
		return repository.save(mapper.toEntity(request));
	}
	
	public Mono<User> findById(final String id){
		return repository.findById(id)
				.switchIfEmpty(Mono.error(
						new ObjectNotFoundException(
								format("Object not found. Id: %s, Type: %s ", id, User.class.getSimpleName()))));
	}

}
