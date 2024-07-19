package br.com.ju.webflux.course.model.response;

public record UserResponse(
		
		String id,
		String name,
		String email,
		String password
		
		) {}
