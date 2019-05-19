package org.bgu.config;

import java.util.Collection;

import org.bgu.config.properties.MongoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {
	
	@Autowired
	private MongoProperties mongoProperties;
	
	@Bean
	@Override
	public MongoClient mongoClient() {
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.alwaysUseMBeans(mongoProperties.isRegisterMbeans());
		builder.sslEnabled(false);
		builder.connectionsPerHost(mongoProperties.getConnectionsPerHost());
		builder.connectTimeout(mongoProperties.getConnectionTimeout());
		builder.minConnectionsPerHost(mongoProperties.getMinConnectionsPerHost());
		builder.maxConnectionLifeTime(mongoProperties.getConnectionLifeTime());
		builder.maxConnectionIdleTime(mongoProperties.getConnectionIdleTime());
		return new MongoClient(new ServerAddress(mongoProperties.getUrl(), mongoProperties.getPort()), MongoCredential.createCredential(mongoProperties.getUsername(), mongoProperties.getDatabase(), mongoProperties.getPassword()), builder.build());
	}
	
	@Bean
	@Override
	public MongoDbFactory mongoDbFactory() {
		return new SimpleMongoDbFactory(mongoClient(), getDatabaseName());
	}
	
	@Bean
	@Override
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoDbFactory());
	}

	@Override
	protected String getDatabaseName() {
		return mongoProperties.getDatabase();
	}

	@Override
	public Collection<String> getMappingBasePackages() {
		return mongoProperties.getMappingBasePackages();
	}
}
