package org.bgu.oauth.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.config.LoggerLevel;
import org.bgu.model.BguOAuth2UserInfo;
import org.bgu.model.GithubBguOAuth2UserInfo;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.model.oauth.BguClientDetails;
import org.bgu.model.oauth.BguOAuth2AuthenticationToken;
import org.bgu.oauth.service.interfaces.BguOAuth2RegistrationService;
import org.bgu.oauth.service.interfaces.BguUserDetailsService;
import org.bgu.oauth.service.interfaces.OAuth2LoginUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GithubOAuthLoginUtil implements OAuth2LoginUtil {

	private final Logger logger = LogManager.getLogger(getClass());
	private final OAuth2RequestFactory requestFactory;
	private final BguClientDetailsService service;
	private final BguUserDetailsService userDetailsService;
	private final BguOAuth2RegistrationService registrationService;
	private final RestTemplate restTemplate;
	
	public GithubOAuthLoginUtil(BguUserDetailsService userDetailsService, BguOAuth2RegistrationService registrationService, BguClientDetailsService service, RestTemplate restTemplate) {
		this.requestFactory = new DefaultOAuth2RequestFactory(service);
		this.service = service;
		this.userDetailsService = userDetailsService;
		this.registrationService = registrationService;
		this.restTemplate = restTemplate;
	}

	/**
	 * Attempts to perform an authorization_code flow given the {@link ClientRegistration} and {@link OAuth2AccessToken} provided
	 */
	@Override
	public OAuth2Authentication attemptAuthentication(ClientRegistration registration, OAuth2AccessToken token) {
		BguClientDetails details = service.loadClientByClientId(registration.getClientId());
		TokenRequest tokenRequest = requestFactory.createTokenRequest(generateAuthorizationParameters(registration), details);
		OAuth2Request request = tokenRequest.createOAuth2Request(details);
		logger.log(LoggerLevel.OAUTH, "Incoming requested scopes: {}", request.getScope());
		BguOAuth2UserInfo info = makeUserInfoRequest(token);
		BguUserDetails userDetails = userDetailsService.loadUserByEmail(info.getEmail());
		return userDetails == null ? new OAuth2Authentication(request, new BguOAuth2AuthenticationToken(registrationService.attemptRegistration(token.getValue(), info))) : new OAuth2Authentication(request, new BguOAuth2AuthenticationToken(userDetailsService.updateUserWithOAuth2Info(token.getValue(), info)));
	}
	
	@Override
	public OAuth2AccessToken retrieveOAuth2AccessToken(ClientRegistration registration, String code, String state) {
		return restTemplate.exchange("https://github.com/login/oauth/access_token", HttpMethod.POST, getAuthenticationHeaders(registration, code, state), OAuth2AccessToken.class).getBody();
	}
	
	/*
	 * =========================================================================
	 * 							Github API Helpers
	 * =========================================================================
	 */
	
	
	/**
	 * @return user information from GH API
	 */
	private BguOAuth2UserInfo makeUserInfoRequest(final OAuth2AccessToken token) {
		return restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl("https://api.github.com/user").queryParam("access_token", token.getValue()).build().toUri(), GithubBguOAuth2UserInfo.class);
	}

	/*
	 * =========================================================================
	 * 							HTTP Header Helpers
	 * =========================================================================
	 */
	private HttpEntity<?> getAuthenticationHeaders(final ClientRegistration registration, final String code, final String state) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", registration.getClientId());
		body.add("client_secret", registration.getClientSecret());
		body.add("code", code);
		body.add("state", state);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return new HttpEntity<MultiValueMap<String, String>>(body, headers);
	}
	
	/**
	 * @return the request information necessary to initiate the authorization_code flow
	 */
	private Map<String, String> generateAuthorizationParameters(final ClientRegistration registration) {
		Map<String, String> params = new HashMap<>();
		params.put("client_id", registration.getClientId());
		params.put("scope", registration.getScopes().stream().collect(Collectors.joining(",")));
		params.put("grant_type", "authorization_code");
		return params;
	}
}
