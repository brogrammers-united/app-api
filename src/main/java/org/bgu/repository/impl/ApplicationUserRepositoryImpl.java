package org.bgu.repository.impl;

import java.util.Optional;

import org.bgu.model.oauth.ApplicationUser;
import org.bgu.repository.ApplicationUserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationUserRepositoryImpl implements ApplicationUserRepository {

	private final MongoTemplate template;
	
	public ApplicationUserRepositoryImpl(final MongoTemplate template) {
		this.template = template;
	}

	@Override
	public Optional<ApplicationUser> loadUserByUsername(String username) {
		return Optional.of(template.findOne(Query.query(Criteria.where("username").is(username)), ApplicationUser.class, "bgu_user"));
	}

	@Override
	public Optional<ApplicationUser> loadUserByEmail(String email) {
		return Optional.of(template.findOne(Query.query(Criteria.where("email").is(email)), ApplicationUser.class, "bgu_user"));
	}
}
