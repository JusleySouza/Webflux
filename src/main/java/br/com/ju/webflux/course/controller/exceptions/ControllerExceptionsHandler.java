package br.com.ju.webflux.course.controller.exceptions;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import br.com.ju.webflux.course.service.exception.ObjectNotFoundException;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;

import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ControllerExceptionsHandler {
	
	@ExceptionHandler(DuplicateKeyException.class)
	public ResponseEntity<Mono<StandardError>> duplicateKeyException(
			DuplicateKeyException ex, ServerHttpRequest request
			){
		return ResponseEntity.badRequest()
				.body(Mono.just(
						StandardError.builder()
						.timestamp(now())
						.path(request.getPath().toString())
						.status(BAD_REQUEST.value())
						.error(BAD_REQUEST.getReasonPhrase())
						.message("E-mail already registered")
						.build()
						));
	}
	
	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<Mono<ValidationError>> validationError(
			WebExchangeBindException ex, ServerHttpRequest request
			){
		ValidationError error = new ValidationError(
				now(), request.getPath().toString(), BAD_REQUEST.value(), "Validation Error", "Error on validation attributes");
		
		for(FieldError x : ex.getBindingResult().getFieldErrors()) {
			error.addError(x.getField(), x.getDefaultMessage());
		}
		
		return ResponseEntity.status(BAD_REQUEST).body(Mono.just(error));
	}
	
	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<Mono<StandardError>> objectNotFoundException(
			ObjectNotFoundException ex, ServerHttpRequest request
			){
		return ResponseEntity.status(NOT_FOUND)
				.body(Mono.just(
						StandardError.builder()
						.timestamp(now())
						.status(NOT_FOUND.value())
						.error(NOT_FOUND.getReasonPhrase())
						.message(ex.getMessage())
						.path(request.getPath().toString())
						.build()
						));
	}

}
