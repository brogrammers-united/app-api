package org.bgu.oauth.service.interfaces;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface OAuth2LoginUtil {

	OAuth2Authentication attemptAuthentication(ClientRegistration registration, OAuth2AccessToken token);
	OAuth2AccessToken retrieveOAuth2AccessToken(ClientRegistration registration, String code, String state);
}
