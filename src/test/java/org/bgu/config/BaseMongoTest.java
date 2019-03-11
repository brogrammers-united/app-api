package org.bgu.config;

import java.util.Collections;

import org.bgu.model.BguRegistrationProvider;
import org.bgu.model.oauth.ApplicationClientDetails;
import org.bgu.model.oauth.ApplicationUser;
import org.bgu.repository.BguClientDetailsRepository;
import org.bgu.repository.impl.BguClientDetailsRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseMongoTest {
	
	protected final BguClientDetailsRepository clientDetailsRepo;
	
	protected final MongoTemplate template;
	
	protected final ApplicationUser user = new ApplicationUser(
				"test_user",
				"password",
				"ROLE_TEST",
				"Test User",
				"test@test.com",
				true,
				true,
				true,
				true,
				Collections.emptyMap(),
				false,
				BguRegistrationProvider.WEB_APP
			);
	
	public BaseMongoTest() {
		/*
		 * Set up test Mongo Template
		 */
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.alwaysUseMBeans(true);
		builder.sslEnabled(false);
		builder.connectionsPerHost(100);
		builder.connectTimeout(0);
		builder.minConnectionsPerHost(0);
		builder.maxConnectionLifeTime(0);
		builder.maxConnectionIdleTime(0);
		this.template = new MongoTemplate(new MongoClient(new ServerAddress("127.0.0.1", 27017), MongoCredential.createCredential("test_admin", "gh_oauth2_test", "Password123!".toCharArray()), builder.build()), "gh_oauth2_test");
		
		/*
		 * Set up BguClientDetailsRepository with test Mongo Template
		 */
		this.clientDetailsRepo = new BguClientDetailsRepositoryImpl(this.template);
	}

	@Before
	public void setUpCollections() {
		createCollectionIfNotExists(ApplicationClientDetails.class);
		createCollectionIfNotExists(ApplicationUser.class);
	}
	
	@After
	public void tearDownCollections() {
		dropCollectionIfExists(ApplicationClientDetails.class);
		dropCollectionIfExists(ApplicationUser.class);
	}
	
	protected final void createCollectionIfNotExists(Class<?> clazz) {
		if (!this.template.collectionExists(clazz))
			this.template.createCollection(clazz);
	}
	
	protected final void dropCollectionIfExists(Class<?> clazz) {
		if (this.template.collectionExists(clazz))
			this.template.dropCollection(clazz);
	}
}
