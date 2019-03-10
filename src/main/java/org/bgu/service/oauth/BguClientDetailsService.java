package org.bgu.service.oauth;

import org.bgu.model.oauth.ApplicationClientDetails;
import org.bgu.repository.BguClientDetailsRepository;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Service
public class BguClientDetailsService implements ClientDetailsService {

	private final BguClientDetailsRepository repo;
	
	public BguClientDetailsService(final BguClientDetailsRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public ApplicationClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		return repo.loadClientDetailsByClientId(clientId).orElseThrow(() -> new ClientRegistrationException("Failed to load client with id [" + clientId + "]"));
	}

}
