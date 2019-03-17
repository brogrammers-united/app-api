package org.bgu.service;

import java.util.Collections;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.config.LoggerLevel;
import org.bgu.model.BguRegistrationProvider;
import org.bgu.model.PersonalInformation;
import org.bgu.model.Phone;
import org.bgu.model.RegistrationForm;
import org.bgu.model.dto.RegistrationResponseDto;
import org.bgu.model.oauth.BguUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	private final Logger logger = LogManager.getLogger(getClass());
	private final MongoTemplate template;
	private final PasswordEncoder encoder;
	private final EmailVerificationService emailVerificationService;
	
	@Autowired
	public RegistrationServiceImpl(final MongoTemplate template, final PasswordEncoder encoder, final EmailVerificationService emailVerificationService) {
		this.template = template;
		this.encoder = encoder;
		this.emailVerificationService = emailVerificationService;
	}
	
	@Override
	public RegistrationResponseDto attemptRegistration(@Valid RegistrationForm form) {
		BguUser user = new BguUser(form.getUsername(), encoder.encode(form.getPassword()), "ROLE_PENDING_VERIFICATION", getNameFromRegistrationForm(form), form.getEmail(), true, false, true, true, Collections.emptyMap(), false, BguRegistrationProvider.WEB_APP);
		Phone phone = new Phone(user.getUsername(), form.getPhone().getNumber(), form.getPhone().getType());
		PersonalInformation info = new PersonalInformation(user.getUsername(), form.getPersonalInformation().getFirstname(), form.getPersonalInformation().getLastname(), form.getPersonalInformation().getBirthday());
		template.save(user, "bgu_user");
		template.save(phone, "bgu_user_phone");
		template.save(info, "bgu_user_info");
		logger.log(LoggerLevel.SECURITY,"Attempting to send verification email to {}", form.getEmail());
		if (emailVerificationService.sendEmailVerification(user)) {
			logger.log(LoggerLevel.SECURITY,"Verification email sent to {}", form.getEmail());
			return new RegistrationResponseDto("Verification email sent to " + user.getEmail());
		}
		logger.log(LoggerLevel.SECURITY,"Failed to send verification email to {}", form.getEmail());
		return null;
	}

	private String getNameFromRegistrationForm(RegistrationForm form) {
		return new StringBuilder().append(form.getPersonalInformation().getFirstname()).append(" ").append(form.getPersonalInformation().getLastname()).toString();
	}
}
