package org.bgu.oauth.service;

import org.bgu.exception.RegistrationRequiredException;
import org.bgu.model.BguOAuth2UserInfo;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.model.oauth.BguUser;
import org.bgu.oauth.service.interfaces.BguOAuth2RegistrationService;
import org.bgu.repository.ApplicationUserRepository;
import org.springframework.stereotype.Service;

@Service
public class BguOAuth2RegistrationServiceImpl implements BguOAuth2RegistrationService {

	private final ApplicationUserRepository repo;
	
	public BguOAuth2RegistrationServiceImpl(ApplicationUserRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public BguUserDetails attemptRegistration(String githubOAuthToken, BguOAuth2UserInfo userInfo) {
		BguUser user = new BguUser(userInfo.getUsername(), userInfo.getEmail(), "ROLE_USER", 
							true, true, true, true, userInfo.getAttributes(), githubOAuthToken);
		return repo.attemptRegistration(user).orElseThrow(RegistrationRequiredException::new);
	}

}
