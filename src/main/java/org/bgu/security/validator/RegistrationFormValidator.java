package org.bgu.security.validator;

import org.bgu.model.RegistrationForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegistrationFormValidator extends BaseValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return RegistrationForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// All fields must not be empty
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "registrationForm.username.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "registrationForm.password.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordMatch", "registrationForm.passwordMatch.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "registrationForm.email.empty");
		
		RegistrationForm form = (RegistrationForm) target;
		
		/*
		 * Username Length Validation
		 */
		if (form.getUsername().length() < 5) {
			errors.rejectValue("username", "registrationForm.username.Min");
		} else if (form.getUsername().length() > 30) {
			errors.rejectValue("username", "registrationForm.username.Max");
		}
		
		/*
		 * Username Content Validation
		 */
		if (!isUsernameValid(form.getUsername())) 
			errors.rejectValue("username", "registrationForm.username.invalid");
		
		/*
		 * Password Length Validation
		 */
		if (form.getPassword().length() < 8)
			errors.rejectValue("password", "registrationForm.password.Min");
		if (form.getPassword().length() > 30) 
			errors.rejectValue("password", "registrationForm.password.Max");
		
		/*
		 * Password Content Validation
		 */
		if (!isPasswordValid(form.getPassword())) {
			if (!passwordContainsUpper(form.getPassword())) {
				errors.rejectValue("password", "registrationForm.password.PasswordContainsUpper");
			} else if (!passwordContainsLower(form.getPassword())) {
				errors.rejectValue("password", "registrationForm.password.PasswordContainsLower");
			} else if (!passwordContainsValidSpecial(form.getPassword())) {
				errors.rejectValue("password", "registrationForm.password.PasswordContainsValidSpecial");
			} else if (!passwordContainsNumber(form.getPassword())) {
				errors.rejectValue("password", "registrationForm.password.PasswordContainsNumber");
			}
		}
		
		/*
		 * Password Match Validation
		 */
		if (!form.getPassword().equals(form.getPasswordMatch()))
			errors.rejectValue("password", "passwords.mismatch");
		
		/*
		 * Email Address format validation
		 */
		if (!isEmailValid(form.getEmail())) 
			errors.rejectValue("email", "registrationForm.email.invalid");
	}

}
