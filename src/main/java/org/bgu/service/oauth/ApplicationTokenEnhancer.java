package org.bgu.service.oauth;

import java.util.HashMap;
import java.util.Map;

import org.bgu.model.oauth.ApplicationUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Service;

@Service
public class ApplicationTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		ApplicationUser user = (ApplicationUser) authentication.getPrincipal();
		final Map<String, Object> additionalInformation = new HashMap<>();

		additionalInformation.put("sub", user.getUsername());
		additionalInformation.put("email", user.getEmail());
		additionalInformation.put("authorities", user.getAuthorities());
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);

		return accessToken;
	}

}
