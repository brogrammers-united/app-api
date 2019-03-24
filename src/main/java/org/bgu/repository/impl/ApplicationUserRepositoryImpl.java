package org.bgu.repository.impl;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.exception.RegistrationRequiredException;
import org.bgu.model.BguOAuth2UserInfo;
import org.bgu.model.oauth.BguUser;
import org.bgu.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationUserRepositoryImpl implements ApplicationUserRepository {

	private final MongoTemplate template;
	private final Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	public ApplicationUserRepositoryImpl(final MongoTemplate template) {
		this.template = template;
	}

	@Override
	public Optional<BguUser> loadUserByUsername(String username) {
		return Optional.of(template.findOne(Query.query(Criteria.where("username").is(username)), BguUser.class, "bgu_user"));
	}

	@Override
	public Optional<BguUser> loadUserByEmail(String email) {
		return Optional.of(template.findOne(Query.query(Criteria.where("email").is(email)), BguUser.class, "bgu_user"));
	}

	@Override
	public Optional<BguUser> updateUser(String accessToken, BguOAuth2UserInfo info) {
		logger.info("Attempting to update user: {}", info);
		BguUser existing = loadUserByEmail(info.getEmail()).orElseThrow(() -> new RegistrationRequiredException("You must register before continuing"));
		return template.update(BguUser.class).replaceWith(BguUser.generateUserFromOAuthInfo(accessToken, existing, info)).findAndReplace();
	}

	@Override
	public Optional<BguUser> attemptRegistration(BguUser user) {
		return Optional.ofNullable(template.save(user, "bgu_user"));
	}
}
