package org.bgu.web;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.bgu.exception.InvalidClientRegistrationException;
import org.bgu.model.dto.UserInfoRequest;
import org.bgu.model.oauth.BguClientRegistrationDetails;
import org.bgu.service.oauth.BguClientRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class AuthController {

	private final RestTemplate restTemplate;
	private final BguClientRegistrationRepository clientRegistrationRepo;
	
	public AuthController(@Autowired final RestTemplate restTemplate, final BguClientRegistrationRepository clientRegistrationRepo) {
		this.restTemplate = restTemplate;
		this.clientRegistrationRepo = clientRegistrationRepo;
	}
	
	@GetMapping(value = "/login/oauth2/code/{registrationId}")
	public ResponseEntity<?> attemptAuthorizationCodeFlow(HttpServletRequest request, @PathVariable("registrationId") String registrationId, @RequestParam("code") final String code, @RequestParam("state") final String state) {
		final ClientRegistration registration = clientRegistrationRepo.findByRegistrationId(registrationId);
		if (registration == null)
			throw new InvalidClientRegistrationException();
		ResponseEntity<OAuth2AccessToken> initialResponse = makeInitialResponse(new BguClientRegistrationDetails(registration), code, state);
		ResponseEntity<UserInfoRequest> userInfo = makeUserInfoRequest(initialResponse.getBody().getValue());
		return userInfo;
	}
	
	/*
	 * Helper method to initiate authorization_code flow
	 */
	private ResponseEntity<OAuth2AccessToken> makeInitialResponse(final BguClientRegistrationDetails details, final String code, final String state) {
		return restTemplate.exchange("https://github.com/login/oauth/access_token", HttpMethod.POST, getInitialRequestHeaders(details, code, state), OAuth2AccessToken.class);
	}
	
	/*
	 * Helper method to get user information from GH API
	 */
	private ResponseEntity<UserInfoRequest> makeUserInfoRequest(final String token) {
		return restTemplate.exchange(UriComponentsBuilder.fromHttpUrl("https://api.github.com/user").queryParam("access_token", token).build().toUri(),HttpMethod.GET, getAuthorizationHeader(token), UserInfoRequest.class);
	}
	
	/**
	 * @return the request necessary to get any protected resource
	 */
	private HttpEntity<?> getAuthorizationHeader(final String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "bearer " + token);
		return new HttpEntity<>(headers);
	}
	
	/**
	 * @return the request information necessary to initiate the authorization_code flow
	 */
	private HttpEntity<?> getInitialRequestHeaders(final BguClientRegistrationDetails details, final String code, final String state) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", details.getClientId());
		body.add("client_secret", details.getClientSecret());
		body.add("code", code);
		body.add("state", state);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return new HttpEntity<MultiValueMap<String, String>>(body, headers);
	}
}
