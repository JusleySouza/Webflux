package br.com.ju.webflux.course.service;

import static java.lang.String.format;

import org.springframework.stereotype.Service;

import br.com.ju.webflux.course.entity.User;
import br.com.ju.webflux.course.mapper.UserMapper;
import br.com.ju.webflux.course.model.request.UserRequest;
import br.com.ju.webflux.course.repository.UserRepository;
import br.com.ju.webflux.course.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
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
		return handleNotFound(repository.findById(id), id);
				
	}
	
	public Flux<User> findAll(){
		return repository.findAll();
	}
	
	public Mono<User> update(final String id, final UserRequest request){
		return findById(id)
				.map(entity -> mapper.toEntity(request, entity))
				.flatMap(repository::save);
	}
	
	public Mono<User> delete(final String id){
		return handleNotFound(repository.findAndRemove(id), id);
				
	}

	private <T> Mono<T> handleNotFound(Mono<T> mono, String id){
		return mono.switchIfEmpty(Mono.error(
				new ObjectNotFoundException(
						format("Object not found. Id: %s, Type: %s ", id, User.class.getSimpleName()))));
	}
	
}
