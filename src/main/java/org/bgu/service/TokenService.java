package org.bgu.service;

import org.springframework.security.core.Authentication;

public interface TokenService {

	String createToken(Authentication authentication);
	String getUsernameFromToken(String token);
	boolean validateToken(String token);
}
