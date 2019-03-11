package org.bgu.web;

import java.util.Collections;
import java.util.Map;

import javax.validation.Valid;

import org.bgu.model.RegistrationForm;
import org.bgu.model.dto.EmailVerificationDto;
import org.bgu.model.dto.RegistrationResponseDto;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.security.validator.RegistrationFormValidator;
import org.bgu.service.EmailVerificationService;
import org.bgu.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

	private final RegistrationService registrationService;
	private final EmailVerificationService emailVerificationService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(new RegistrationFormValidator());
	}
	
	@Autowired
	public RegistrationController(final RegistrationService registrationService, final EmailVerificationService emailVerificationService) {
		this.registrationService = registrationService;
		this.emailVerificationService = emailVerificationService;
	}
	
	@PostMapping(value="/register", produces=MediaType.APPLICATION_JSON_UTF8_VALUE, consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<RegistrationResponseDto> attemptRegistration(@Valid @RequestBody final RegistrationForm form) {
		final RegistrationResponseDto dto = registrationService.attemptRegistration(form);
		if (dto == null)
			return ResponseEntity.badRequest().body(new RegistrationResponseDto("Something went wrong while sending verification email to " + form.getEmail()));
		return ResponseEntity.ok(dto);
	}
	
	@GetMapping(value="/verify/{token}/{email}", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Map<String, String>> attemptEmailVerification(@PathVariable("token") final String token, @PathVariable("email") final String email) {
		BguUserDetails user = emailVerificationService.attemptEmailVerification(new EmailVerificationDto(token, email));
		if (user == null) {
			return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Failed to verify email " + email));
		}
		return ResponseEntity.ok(Collections.singletonMap("message", "Account verified!"));
	}
}
