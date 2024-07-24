package br.com.ju.webflux.course.mapper.impl;

import br.com.ju.webflux.course.entity.User;
import br.com.ju.webflux.course.mapper.UserMapper;
import br.com.ju.webflux.course.model.request.UserRequest;
import br.com.ju.webflux.course.model.response.UserResponse;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-19T19:57:41-0300",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 17.0.2 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        if ( request.name() != null ) {
            user.name( request.name() );
        }
        if ( request.email() != null ) {
            user.email( request.email() );
        }
        if ( request.password() != null ) {
            user.password( request.password() );
        }

        return user.build();
    }
    
    @Override
	public User toEntity(UserRequest request, User entity) {
		if ( request == null ) {
            return entity;
        }

        if ( request.name() != null ) {
        	entity.setName( request.name() );
        }
        if ( request.email() != null ) {
        	entity.setEmail( request.email() );
        }
        if ( request.password() != null ) {
        	entity.setPassword( request.password() );
        }

        return entity;
	}

	@Override
	public UserResponse toResponse(User entity) {
		if (entity == null) {
			return null;
		}
		
		String id = null;
		String name = null;
		String email = null;
		String password = null;
		
		if ( entity.getId() != null ) {
            id = entity.getId();
        }
		if ( entity.getName() != null ) {
           name = entity.getName();
        }
        if ( entity.getEmail() != null ) {
            email = entity.getEmail();
        }
        if ( entity.getPassword() != null ) {
            password = entity.getPassword();
        }
        
        UserResponse userResponse = new UserResponse(id, name, email, password);
		
		return userResponse;
	}

}

