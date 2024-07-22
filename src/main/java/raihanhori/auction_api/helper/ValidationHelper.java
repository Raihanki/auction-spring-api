package raihanhori.auction_api.helper;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Component
public class ValidationHelper {

	@Autowired
	private Validator validator;
	
	public void validate(Object request) {
		Set<ConstraintViolation<Object>> violations = validator.validate(request);
		
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}
	
}
