package raihanhori.auction_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;
import raihanhori.auction_api.helper.ErrorApiResponse;

@ControllerAdvice
public class ErrorController {

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorApiResponse> constraintViolation(ConstraintViolationException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErrorApiResponse.builder().status(400).message(exception.getMessage()).build());
	}
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorApiResponse> constraintViolation(ResponseStatusException exception) {
		return ResponseEntity.status(exception.getStatusCode())
				.body(ErrorApiResponse.builder().status(exception.getStatusCode().value()).message(exception.getReason()).build());
	}

}
