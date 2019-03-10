package org.bgu.security.validator;

import java.util.regex.Pattern;

public class BaseValidator {

	protected BaseValidator() {}
	
	protected static final Pattern validTextOnlyPattern = Pattern.compile("[a-zA-Z]");
	protected static final Pattern validNumericPattern = Pattern.compile("[0-9]");
	protected static final Pattern validEmail = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
	protected static final Pattern validUsername = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()].*");
	protected static final Pattern validPassword = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$!%^&+=]).*");
	
	public static final boolean isEmailValid(String email) {
		return validEmail.matcher(email).matches();
	}
	
	public static final boolean isUsernameValid(String username) {
		return validUsername.matcher(username).matches();
	}

	public static final boolean isPasswordValid(String password) {
		return validPassword.matcher(password).matches();
	}
	
	public static final boolean passwordContainsUpper(String password) {
		return Pattern.matches("(?=.*[A-Z]).*", password);
	}
	
	public static final boolean passwordContainsLower(String password) {
		return Pattern.matches("(?=.*[a-z]).*", password);
	}
	
	public static final boolean passwordContainsValidSpecial(String password) {
		return Pattern.matches("(?=.*[@#$!%^&+=]).*", password);
	}
	
	public static final boolean passwordContainsNumber(String password) {
		return Pattern.matches("(?=.*[0-9]).*", password);
	}
}
