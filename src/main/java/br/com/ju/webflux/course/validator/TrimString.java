package br.com.ju.webflux.course.validator;

import jakarta.validation.Payload;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { TrimStringValidator.class })
@Target(FIELD)
@Retention(RUNTIME)
public @interface TrimString {
	
	String message() default "Field cannot have blank spaces at the beginning or at and";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default{};

}
