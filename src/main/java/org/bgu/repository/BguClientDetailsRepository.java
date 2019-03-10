package org.bgu.repository;

import java.util.Optional;

import org.bgu.model.oauth.ApplicationClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;

public interface BguClientDetailsRepository {

	ClientDetails save(ClientDetails details);
	Optional<ApplicationClientDetails> loadClientDetailsByClientId(String clientId);
	Optional<ApplicationClientDetails> loadClientDetailsByRegistrationId(String registrationId);
}
