package org.bgu.service;

import javax.validation.Valid;

import org.bgu.model.RegistrationForm;
import org.bgu.model.dto.RegistrationResponseDto;

public interface RegistrationService {

	RegistrationResponseDto attemptRegistration(@Valid RegistrationForm form);
}
