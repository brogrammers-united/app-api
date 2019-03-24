package org.bgu.oauth.service.interfaces;

import org.bgu.model.BguOAuth2UserInfo;
import org.bgu.model.interfaces.BguUserDetails;

public interface BguOAuth2RegistrationService {

	BguUserDetails attemptRegistration(String githubOAuthToken, BguOAuth2UserInfo userInfo);
}
