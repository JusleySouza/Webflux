package br.com.ju.webflux.course.mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.ju.webflux.course.entity.User;
import br.com.ju.webflux.course.model.request.UserRequest;

@Mapper(
		componentModel = "spring",
		nullValuePropertyMappingStrategy = IGNORE,
		nullValueCheckStrategy = ALWAYS
		)
public interface UserMapper {
	
	@Mapping(target = "id", ignore = true)
	User toEntity(final UserRequest request);

}
