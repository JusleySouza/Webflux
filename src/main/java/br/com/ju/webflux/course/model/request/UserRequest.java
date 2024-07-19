package br.com.ju.webflux.course.model.request;

public record UserRequest(
		
		String name,
		String email,
		String password
		
		) {}
