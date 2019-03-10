package org.bgu.config;

import org.bgu.config.properties.MongoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableConfigurationProperties({MongoProperties.class})
public class PropertiesConfiguration {
	
	@Autowired
	private ApplicationContext context;

	@Bean(name= {"mongoProperties", "mongoProps"})
	@Primary
	public MongoProperties mongoProperties() {
		return new MongoProperties(context);
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setLocations(new ClassPathResource("application.properties"), new ClassPathResource("application.yml"), new ClassPathResource("mongodb.properties"), new ClassPathResource("mongodb.yml"));
		configurer.setIgnoreResourceNotFound(true);
		configurer.setIgnoreUnresolvablePlaceholders(true); 
		configurer.setLocalOverride(true);
		return configurer;
	}
}
