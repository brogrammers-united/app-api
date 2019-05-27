package org.bgu;

import org.bgu.config.properties.KeyStoreProperties;
import org.bgu.config.properties.MongoProperties;
import org.bgu.exception.ApplicationExceptionHandler;
import org.bgu.oauth.service.BguClientDetailsService;
import org.bgu.oauth.service.BguClientRegistrationRepository;
import org.bgu.oauth.service.BguTokenStore;
import org.bgu.oauth.service.BguUserDetailsServiceImpl;
import org.bgu.oauth.service.interfaces.BguUserDetailsService;
import org.bgu.repository.AccessTokenRepository;
import org.bgu.repository.ApplicationUserRepository;
import org.bgu.repository.BguClientDetailsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthzServerApplicationTests {

	@Autowired
	private ApplicationContext context;

	private ApplicationContextRunner runner;

	@Before
	public void setUpRunner() {
		runner = new ApplicationContextRunner().withParent(context);
	}

	@Test
	public void contextLoads() {
		runner.run(context -> {
			assertThat(context).hasSingleBean(WebClient.Builder.class);
			assertThat(context).hasSingleBean(MongoTemplate.class);
			assertThat(context.getBean("oauthMongoTemplate")).isSameAs(context.getBean(MongoTemplate.class));
			assertThat(context).hasSingleBean(KeyStoreProperties.class);
			assertThat(context).hasSingleBean(MongoProperties.class);
			assertThat(context).hasSingleBean(ApplicationExceptionHandler.class);
			assertThat(context).hasSingleBean(BguTokenStore.class);
			assertThat(context).hasSingleBean(AccessTokenRepository.class);
			assertThat(context).hasSingleBean(ApplicationUserRepository.class);
			assertThat(context).hasSingleBean(BguClientDetailsRepository.class);
		});
	}


}
