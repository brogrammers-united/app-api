package org.bgu.security.validator;

import java.util.regex.Pattern;

import org.bgu.model.Phone;
import org.bgu.model.PhoneType;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PhoneValidator implements Validator {

	private final Pattern validPhonePattern = Pattern.compile("(?:\\d{3}-){2}\\d{4}"); 
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Phone.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// All fields must not be empty
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "number", "phone.number.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "phone.type.empty");
		Phone phone = (Phone) target;
		
		// Phone Type must match valid PhoneType enum
		if (!(phone.getType() instanceof PhoneType))
			errors.rejectValue("type", "phone.type.invalid");
		
		// Phone Number must be in format XXX-XXX-XXXX
		if (phone.getNumber().length() > 12)
			errors.rejectValue("number", "phone.number.invalid");
		
		// Phone Number must match phone number regex
		if (!validPhoneNumber(phone.getNumber()))
			errors.rejectValue("number", "phone.number.invalid");
	}

	private boolean validPhoneNumber(String phoneNumber) {
		return validPhonePattern.matcher(phoneNumber).matches();
	}
	
}
