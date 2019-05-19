package org.bgu.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.config.LoggerLevel;
import org.bgu.oauth.service.BguTokenStore;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final Logger logger = LogManager.getLogger(getClass());
	private final HttpCookieOAuth2AuthorizationRequestRepository requestRepo;
	private final BguTokenStore tokenStore;
	
	public OAuth2AuthenticationSuccessHandler(HttpCookieOAuth2AuthorizationRequestRepository requestRepo, BguTokenStore tokenStore) {
		this.requestRepo = requestRepo;
		this.tokenStore = tokenStore;
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		logger.log(LoggerLevel.AUTHENTICATION, "{} authenticated successfully at {}", authentication.getName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")));
		OAuth2Authentication auth = (OAuth2Authentication) authentication;
		logger.log(LoggerLevel.AUTHENTICATION, "{}", auth);
		clearAuthenticationAttributes(request, response);
		final String token = tokenStore.getAccessToken(auth).getValue();
		SecurityContextHolder.getContext().setAuthentication(auth);
		logger.log(LoggerLevel.SECURITY, "Security context set!");
		CookieUtils.addCookie(response, "api_token", token, (60 * 15)); // Cookie valid for 15 minutes
		logger.log(LoggerLevel.SECURITY, "Cookie set!");
		getRedirectStrategy().sendRedirect(request, response, UriComponentsBuilder.fromHttpUrl("http://localhost:8080/user").toUriString());
	}
	
	

	private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		requestRepo.removeAuthorizationRequestCookies(request, response);
	}
}
