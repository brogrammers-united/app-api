package org.bgu.repository.impl;

import java.util.Optional;

import org.bgu.model.oauth.ApplicationClientDetails;
import org.bgu.repository.BguClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Repository;

@Repository
public class BguClientDetailsRepositoryImpl implements BguClientDetailsRepository {

	private final MongoTemplate template;
	
	@Autowired
	public BguClientDetailsRepositoryImpl(final MongoTemplate template) {
		this.template = template;
	}

	@Override
	public ClientDetails save(ClientDetails details) {
		return template.save(details, "bgu_client_details");
	}
	
	@Override
	public Optional<ApplicationClientDetails> loadClientDetailsByClientId(String clientId) {
		return Optional.of(template.findOne(Query.query(Criteria.where("clientId").is(clientId)), ApplicationClientDetails.class, "bgu_client_details"));
	}

	@Override
	public Optional<ApplicationClientDetails> loadClientDetailsByRegistrationId(String registrationId) {
		return Optional.of(template.findOne(Query.query(Criteria.where("registrationId").is(registrationId)), ApplicationClientDetails.class, "bgu_client_details"));
	}
	
	
}
