package org.bgu.service;

import org.bgu.model.dto.EmailVerificationDto;
import org.bgu.model.interfaces.Verifiable;

public interface EmailService {

	boolean sendEmail(Verifiable user, EmailVerificationDto dto);
}
