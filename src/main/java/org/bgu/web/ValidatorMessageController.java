package org.bgu.web;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class ValidatorMessageController {

	@Autowired
	private MessageSource messageSource;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
		logger.warn("Handling exception: {}", ex.getClass().getName());
		ObjectError err = null;
		String errorMessage = null;
		if (ex.getBindingResult().hasFieldErrors()) {
			err = ex.getBindingResult().getFieldError();
			errorMessage = messageSource.getMessage(err.getCode(), err.getArguments(), LocaleContextHolder.getLocale());
		} else {
			err = ex.getBindingResult().getGlobalError();
			errorMessage = messageSource.getMessage(err, LocaleContextHolder.getLocale());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", errorMessage));
	}
}
