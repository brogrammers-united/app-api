package org.bgu.service;

import org.bgu.model.dto.EmailVerificationDto;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.model.interfaces.Verifiable;

public interface VerificationTokenService {

	EmailVerificationDto generateVerificationToken(Verifiable user);
	BguUserDetails verifyToken(EmailVerificationDto dto);
}
