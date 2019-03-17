package org.bgu.web.resource;

import org.bgu.model.interfaces.BguUserDetails;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableResourceServer
public class UserInfoController {

	@GetMapping(value="/user", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BguUserDetails getUserInfo(OAuth2Authentication authentication) {
		return (BguUserDetails) authentication.getPrincipal();
	}
}
