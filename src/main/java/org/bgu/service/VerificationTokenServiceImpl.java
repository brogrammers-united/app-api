package org.bgu.service;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.config.LoggerLevel;
import org.bgu.model.UserAuthority;
import org.bgu.model.dto.EmailVerificationDto;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.model.interfaces.Verifiable;
import org.bgu.model.oauth.BguUser;
import org.bgu.model.oauth.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

	private final Logger logger = LogManager.getLogger(getClass());
	private final KeyPair pair;
	private final MongoTemplate template;
	
	public VerificationTokenServiceImpl(@Autowired final MongoTemplate template, @Autowired final KeyStoreService keyStoreService) {
		this.template = template;
		this.pair = keyStoreService.getKeyPair();
	}
	
	@Override
	public EmailVerificationDto generateVerificationToken(Verifiable user) {
		final String id = UUID.randomUUID().toString() + UUID.randomUUID().toString();
		final LocalDateTime timestamp = LocalDateTime.now().plusMinutes(15);
		final String token = generateJwtVerificationToken(user, id, timestamp);
		final VerificationToken verificationToken = new VerificationToken(token, user.getEmail(), timestamp);
		logger.log(LoggerLevel.SECURITY, "Generating email verification token for user {} at {}", user.getUsername(), user.getEmail());
		template.save(verificationToken, "verification_token");
		return new EmailVerificationDto(token, user.getEmail());
	}

	@Override
	public BguUserDetails verifyToken(EmailVerificationDto dto) {
		logger.log(LoggerLevel.SECURITY, "Incoming email verification coming from {}", dto.getEmail());
		final VerificationToken token = template.findOne(Query.query(Criteria.where("email").is(dto.getEmail())), VerificationToken.class, "verification_token");
		if (isEmailVerificationTokenValid(token) && dto.getVerification().equals(token.getVerification())) {
			BguUser user = template.findOne(Query.query(Criteria.where("email").is(dto.getEmail())), BguUser.class, "bgu_user");
			user.setAccountNonLocked(true);
			user.setAuthorities(Arrays.asList(new UserAuthority("ROLE_USER")));
			user = template.update(BguUser.class).matching(Query.query(Criteria.where("email").is(dto.getEmail()))).replaceWith(user).findAndReplaceValue();
			if (template.remove(token, "verification_token").getDeletedCount() != 1) {
				logger.log(LoggerLevel.SECURITY, "EMAIL VERIFICATION FAILED FOR {}", dto.getEmail());
				return null;
			}
			logger.log(LoggerLevel.SECURITY, "{} email verification complete", user.getUsername());
			return user;
		}
		return null;
	}
	
	private boolean isEmailVerificationTokenValid(final VerificationToken token) {
		final Claims claims = Jwts.parser().setSigningKey(this.pair.getPublic()).parseClaimsJws(token.getVerification()).getBody();
		return new Date().before(claims.getExpiration());
	}
	
	private String generateJwtVerificationToken(Verifiable user, String id, LocalDateTime timestamp) {
		return Jwts.builder()
				   .setId(id)
				   .setAudience(user.getEmail())
				   .setExpiration(Date.from(timestamp.atZone(ZoneId.systemDefault()).toInstant()))
				   .signWith(pair.getPrivate())
				   .compact();
	}

}
