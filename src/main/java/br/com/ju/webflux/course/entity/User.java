package br.com.ju.webflux.course.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.Generated;

@Data
@Builder
@Document
@Generated
public class User {
	
	@Id
	private String id;
	private String name;
	@Indexed(unique = true)
	private String email;
	private String password;
	
}
