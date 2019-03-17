package org.bgu.config;

import java.io.IOException;
import java.util.Properties;

import org.bgu.model.PersonalInformation;
import org.bgu.model.Phone;
import org.bgu.model.oauth.BguClientDetails;
import org.bgu.model.oauth.BguClientRegistration;
import org.bgu.model.oauth.BguUser;
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
	private final Properties props = getTestProperties();
	protected final MongoTemplate template;
	
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
		this.template = new MongoTemplate(new MongoClient(new ServerAddress(props.getProperty("mongodb.url"), Integer.valueOf(props.getProperty("mongodb.port"))), 
						MongoCredential.createCredential(props.getProperty("mongodb.username"), props.getProperty("mongodb.database"), props.get("mongodb.password").toString().toCharArray()), builder.build()), 
					    props.getProperty("mongodb.database"));
		
		
		/*
		 * Set up BguClientDetailsRepository with test Mongo Template
		 */
		this.clientDetailsRepo = new BguClientDetailsRepositoryImpl(this.template);
	}

	@Before
	public void setUpCollections() {
		createCollectionIfNotExists(BguClientDetails.class);
		createCollectionIfNotExists(BguUser.class);
		createCollectionIfNotExists(BguClientRegistration.class);
		createCollectionIfNotExists(PersonalInformation.class);
		createCollectionIfNotExists(Phone.class);
	}
	
	@After
	public void tearDownCollections() {
		dropCollectionIfExists(BguClientDetails.class);
		dropCollectionIfExists(BguUser.class);
		dropCollectionIfExists(BguClientRegistration.class);
		dropCollectionIfExists(Phone.class);
		dropCollectionIfExists(PersonalInformation.class);
	}
	
	protected final void createCollectionIfNotExists(Class<?> clazz) {
		if (!this.template.collectionExists(clazz))
			this.template.createCollection(clazz);
	}
	
	protected final void dropCollectionIfExists(Class<?> clazz) {
		if (this.template.collectionExists(clazz))
			this.template.dropCollection(clazz);
	}
	
	private final Properties getTestProperties() {
		Properties props = new Properties();
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mongodb.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load mongodb.properties");
		}
		return props;
	}
}
