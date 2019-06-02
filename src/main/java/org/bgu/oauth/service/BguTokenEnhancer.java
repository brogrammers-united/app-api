package org.bgu.oauth.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.model.interfaces.BguUserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class BguTokenEnhancer implements TokenEnhancer {
	
	private final Logger logger = LogManager.getLogger(getClass());

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		logger.info("Enhancing token for {}", ((BguUserDetails) authentication.getPrincipal()).getUsername());
		if (authentication.getOAuth2Request().getGrantType().equals("client_credentials"))
			return accessToken;
		BguUserDetails user = (BguUserDetails) authentication.getPrincipal();
		final Map<String, Object> additionalInformation = new HashMap<>();
		additionalInformation.put("sub", user.getUsername());
		additionalInformation.put("uid", user.getUserId());
		additionalInformation.put("email", user.getEmail());
		additionalInformation.put("authorities", user.getAuthorities());
		additionalInformation.put("name", user.getName());
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);

		return accessToken;
	}

}
