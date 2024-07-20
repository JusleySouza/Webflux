package br.com.ju.webflux.course.controller.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ju.webflux.course.controller.UserController;
import br.com.ju.webflux.course.model.request.UserRequest;
import br.com.ju.webflux.course.model.response.UserResponse;
import br.com.ju.webflux.course.service.UserService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserControllerImpl implements UserController{
	
	private final UserService service;

	@Override
	public ResponseEntity<Mono<Void>> save(final UserRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request).then());
	}

	@Override
	public ResponseEntity<Mono<UserResponse>> find(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Flux<UserResponse>> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Mono<UserResponse>> update(String id, UserRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Mono<Void>> delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
