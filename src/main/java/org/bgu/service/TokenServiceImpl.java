package org.bgu.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.config.LoggerLevel;
import org.bgu.model.oauth.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;

@Service
public class TokenServiceImpl implements TokenService {

	private final Logger logger = LogManager.getLogger(getClass());
	private final KeyStoreService keyService;
	private static final long TOKEN_EXPIRY = 1000 * 60 * 60 * 24 * 10; // 10 days  
	
	@Autowired
	public TokenServiceImpl(final KeyStoreService keyService) {
		this.keyService = keyService;
	}
	
	@Override
	public String createToken(Authentication authentication) {
		ApplicationUser user = (ApplicationUser) authentication;
		final Date now = new Date();
		return Jwts.builder().signWith(keyService.getKeyPair().getPrivate())
				   .setSubject(user.getUsername())
				   .setIssuedAt(now)
				   .setExpiration(new Date(now.getTime() + TOKEN_EXPIRY))
				   .compact();
	}

	@Override
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(keyService.getKeyPair().getPublic()).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			logger.log(LoggerLevel.SECURITY, "JWT validation failed at {}. Exception was {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")), e.getClass().getName());
			logger.log(LoggerLevel.SECURITY, "Cause was {}. {}", e.getCause(), e.getMessage());
			return false;
		}
	}

	@Override
	public String getUsernameFromToken(String token) {
		return Jwts.parser().setSigningKey(keyService.getKeyPair().getPublic()).parseClaimsJws(token).getBody().getSubject();
	}

}
