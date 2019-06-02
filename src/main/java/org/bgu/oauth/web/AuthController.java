package org.bgu.oauth.web;

import org.bgu.exception.InvalidClientRegistrationException;
import org.bgu.oauth.service.BguClientRegistrationRepository;
import org.bgu.oauth.service.BguTokenStore;
import org.bgu.oauth.service.interfaces.OAuth2LoginUtil;
import org.bgu.security.OAuth2AuthenticationSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AuthController {

	private final BguClientRegistrationRepository clientRegistrationRepo;
	private final OAuth2LoginUtil loginUtil;
	private final BguTokenStore tokenStore;
	private final TokenEnhancer tokenEnhancer;
	private final OAuth2AuthenticationSuccessHandler successHandler;
	
	public AuthController(BguClientRegistrationRepository clientRegistrationRepo, OAuth2LoginUtil loginUtil, BguTokenStore tokenStore, TokenEnhancer tokenEnhancer, OAuth2AuthenticationSuccessHandler successHandler) {
		this.clientRegistrationRepo = clientRegistrationRepo;
		this.loginUtil = loginUtil;
		this.tokenStore = tokenStore;
		this.tokenEnhancer = tokenEnhancer;
		this.successHandler = successHandler;
	}
	
	@GetMapping(value = "/login/oauth2/code/{registrationId}")
	public void attemptAuthorizationCodeFlow(HttpServletRequest request, HttpServletResponse response, @PathVariable("registrationId") String registrationId, @RequestParam("code") final String code, @RequestParam("state") final String state) {
		// Locate the ClientRegistration object from Mongo
		final ClientRegistration registration = clientRegistrationRepo.findByRegistrationId(registrationId);
		if (registration == null)
			throw new InvalidClientRegistrationException();

		// Given a valid ClientRegistration, contact Github servers for OAuth token
		OAuth2AccessToken accessToken = loginUtil.retrieveOAuth2AccessToken(registration, code, state);

		OAuth2Authentication authentication = loginUtil.attemptAuthentication(registration, accessToken);
		accessToken = tokenEnhancer.enhance(accessToken, authentication);
		tokenStore.storeAccessToken(accessToken, authentication);
		try {
			successHandler.onAuthenticationSuccess(request, response, authentication);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
}
