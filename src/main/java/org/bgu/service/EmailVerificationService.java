package org.bgu.service;

import org.bgu.model.dto.EmailVerificationDto;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.model.interfaces.Verifiable;

public interface EmailVerificationService {

	BguUserDetails attemptEmailVerification(EmailVerificationDto dto);
	boolean sendEmailVerification(Verifiable user);
}
