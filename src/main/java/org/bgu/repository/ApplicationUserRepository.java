package org.bgu.repository;

import java.util.Optional;

import org.bgu.model.oauth.ApplicationUser;

public interface ApplicationUserRepository {

	Optional<ApplicationUser> loadUserByUsername(String username);
	Optional<ApplicationUser> loadUserByEmail(String email);
}
