package org.bgu.security.validator;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.bgu.model.PersonalInformation;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PersonalInformationValidator extends BaseValidator implements Validator {

	private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	
	@Override
	public boolean supports(Class<?> clazz) {
		return PersonalInformation.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// All fields must not be empty
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "info.firstname.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "info.lastname.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthday", "info.birthday.empty");
		
		PersonalInformation info = (PersonalInformation) target;
		
		// Birthday string must be valid
		if (!isValidDate(info.getBirthday()))
			errors.rejectValue("birthday", "info.birthday.invalid");
		if (!validTextOnlyPattern.matcher(info.getFirstname()).matches())
			errors.rejectValue("firstname", "info.firstname.invalid");
		if (!validTextOnlyPattern.matcher(info.getLastname()).matches())
			errors.rejectValue("lastname", "info.lastname.invalid");
	}

	private boolean isValidDate(String date) {
		try {
			dateFormat.parse(date);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
}
