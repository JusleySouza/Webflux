package br.com.ju.webflux.course.controller.exceptions;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.Generated;

@Data
@Builder
@Generated
public class StandardError implements Serializable{
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	private LocalDateTime timestamp;
	private String path;
	private Integer status;
	private String error;
	private String message;

}
