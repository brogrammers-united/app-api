package org.bgu.service.oauth;

import java.util.HashMap;
import java.util.Map;

import org.bgu.model.oauth.BguUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Service;

@Service
public class BguTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		if (authentication.getOAuth2Request().getGrantType().equalsIgnoreCase("password")) {
			BguUser user = (BguUser) authentication.getUserAuthentication();
			final Map<String, Object> additionalInformation = new HashMap<>();

			additionalInformation.put("sub", user.getUsername());
			additionalInformation.put("email", user.getEmail());
			additionalInformation.put("authorities", user.getAuthorities());
			additionalInformation.put("name", user.getName());
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);

			return accessToken;
		} 
		return accessToken;
	}

}
