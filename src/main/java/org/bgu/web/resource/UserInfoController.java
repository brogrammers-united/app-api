package org.bgu.web.resource;

import org.bgu.model.oauth.BguOAuth2AuthenticationToken;
import org.bgu.oauth.service.BguTokenStore;
import org.bgu.oauth.service.interfaces.BguUserDetailsService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {
	
	private final BguTokenStore tokenStore;
	private final BguUserDetailsService userDetailsService;

	public UserInfoController(BguTokenStore tokenStore, BguUserDetailsService userDetailsService) {
		this.tokenStore = tokenStore;
		this.userDetailsService = userDetailsService;
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping(value="/user", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BguOAuth2AuthenticationToken getUserInfo(@CookieValue(name = "api_token") final String token) {
		final OAuth2Authentication authentication = tokenStore.readAuthentication(token);
		return new BguOAuth2AuthenticationToken(userDetailsService.loadUserByUsername(authentication.getName()));
	}
}
