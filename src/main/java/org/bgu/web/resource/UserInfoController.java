package org.bgu.web.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.config.LoggerLevel;
import org.bgu.oauth.service.BguTokenStore;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserInfoController {
	
	private final Logger logger = LogManager.getLogger(getClass());
	private final BguTokenStore tokenStore;
	
	public UserInfoController(BguTokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping(value="/user", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Principal getUserInfo(@CookieValue(name = "api_token") final String token) {
		final OAuth2Authentication authentication = tokenStore.readAuthentication(token);
		logger.log(LoggerLevel.OAUTH, "Retrieving user information for {}", authentication);
		return authentication;
	}
}
