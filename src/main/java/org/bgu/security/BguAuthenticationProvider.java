package org.bgu.security;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.config.LoggerLevel;
import org.bgu.exception.EmailVerificationException;
import org.bgu.exception.FailedAuthenticationAttemptException;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.security.validator.BaseValidator;
import org.bgu.service.oauth.interfaces.BguUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BguAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private final Logger logger = LogManager.getLogger(getClass());
	private final PasswordEncoder encoder;
	private final BguUserDetailsService service;
	
	public BguAuthenticationProvider(final PasswordEncoder encoder, final BguUserDetailsService service) {
		this.encoder = encoder;
		this.service = service;
	}
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		/*
		 * Check that passwords match
		 */
		if (!encoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
			throw new FailedAuthenticationAttemptException("Invalid Credentials");
		}
		
		/*
		 * User must have verified email address
		 */
		if (!userDetails.isAccountNonLocked())
			throw new EmailVerificationException("You must verify email before continuing");
		
	}

	@Override
	protected BguUserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		logger.log(LoggerLevel.AUTHENTICATION, "{} attempting authentication at {}", username, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")));
		// Determine if authentication request is a username or an email
		if (BaseValidator.isEmailValid(username))
			return service.loadUserByEmail(username);
		else if (BaseValidator.isUsernameValid(username))
			return service.loadUserByUsername(username);
		else 
			throw new FailedAuthenticationAttemptException("Invalid format for username/email");
	}

}
