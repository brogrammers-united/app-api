package org.bgu.service;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import org.bgu.config.properties.MailProperties;
import org.bgu.model.dto.EmailVerificationDto;
import org.bgu.model.interfaces.Verifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
	
	private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	private final JavaMailSender mailSender;
	private final MailProperties mailProps;
	
	public EmailServiceImpl(@Autowired final JavaMailSender mailSender, @Autowired final MailProperties mailProps) {
		this.mailSender = mailSender;
		this.mailProps = mailProps;
	}

	@Override
	public boolean sendEmail(Verifiable user, EmailVerificationDto dto) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
			helper.setTo(user.getEmail());
			helper.setFrom(mailProps.getFromAddress());
			helper.setText("Click the link to complete verification: <a href=\"http://localhost:8080/verify/" + dto.getVerification() + "/" + dto.getEmail() + "\">here!</a>", true);
			helper.setSubject(mailProps.getSubjectLine());
			helper.setValidateAddresses(true);
			mailSender.send(message);
			return true;
		} catch (Exception  e) {
			logger.error("Failed to send email to {}: {}", user.getUsername(), e);
			throw new RuntimeException(e);
		}
	}
	
}
