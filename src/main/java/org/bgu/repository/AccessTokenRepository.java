package org.bgu.repository;

import java.util.List;
import java.util.Optional;

import org.bgu.model.oauth.AccessToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccessTokenRepository extends MongoRepository<AccessToken, ObjectId>{

	List<AccessToken> findByClientId(String clientId);
	List<AccessToken> findByClientIdAndUsername(String clientId, String username);
	Optional<AccessToken> findByTokenId(String tokenId);
	Optional<AccessToken> findByRefreshToken(String refreshToken);
	Optional<AccessToken> findByAuthenticationId(String authenticationId);
	long deleteByTokenId(String tokenId);
}
