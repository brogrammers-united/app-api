package org.bgu.repository;

import java.util.Optional;

import org.bgu.model.oauth.RefreshToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, ObjectId>{

	Optional<RefreshToken> findOptionalByTokenId(String tokenId);
	long deleteByTokenId(String tokenId);
}
