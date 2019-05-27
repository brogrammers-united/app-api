package org.bgu.oauth.service;

import org.bgu.exception.InvalidRequestException;
import org.bgu.model.BguOAuth2UserInfo;
import org.bgu.model.BguUser;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.oauth.service.interfaces.BguOAuth2RegistrationService;
import org.bgu.repository.ApplicationUserRepository;
import org.springframework.stereotype.Service;

/**
 * @author William Gentry
 */
@Service
public class BguOAuth2RegistrationServiceImpl implements BguOAuth2RegistrationService {

    private final ApplicationUserRepository userRepository;

    public BguOAuth2RegistrationServiceImpl(ApplicationUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public BguUserDetails attemptRegistration(String githubOAuthToken, BguOAuth2UserInfo userInfo) {
        return userRepository.attemptRegistration(BguUser.generateUserFromOAuthInfo(githubOAuthToken, userInfo)).orElseThrow(InvalidRequestException::new);
    }
}
