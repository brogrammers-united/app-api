package org.bgu.service;

import org.bgu.model.dto.EmailVerificationDto;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.model.interfaces.Verifiable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private VerificationTokenService verificationTokenService;

	@Override
	public BguUserDetails attemptEmailVerification(EmailVerificationDto dto) {
		return verificationTokenService.verifyToken(dto);
	}

	@Override
	public boolean sendEmailVerification(Verifiable user) {
		return emailService.sendEmail(user, verificationTokenService.generateVerificationToken(user));
	}

}
