package org.bgu.oauth;

import org.bgu.config.BaseMongoTest;
import org.bgu.model.oauth.BguClientDetails;
import org.bgu.model.oauth.BguClientRegistration;
import org.bgu.oauth.service.BguClientRegistrationRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.util.StringUtils;

import java.util.Collections;

import static org.junit.Assert.*;

public class BguClientRegistrationTest extends BaseMongoTest {

	private BguClientRegistration appClient;
	private BguClientDetails clientDetails;
	
	private BguClientRegistrationRepository repo = new BguClientRegistrationRepository(template);
	
	@Before
	public void setUpClientRegistration() {
		appClient = new BguClientRegistration(
					"github-api",
					"Github",
					System.getenv("GH_API_CLIENT_ID"),
					System.getenv("GH_API_CLIENT_SECRET"),
					"https://github.com/login/oauth/authorize",
					AuthorizationGrantType.AUTHORIZATION_CODE,
					"openid,profile,email,user,repo",
					AuthenticationMethod.FORM,
					ClientAuthenticationMethod.POST,
					"{baseUrl}/login/oauth2/code/{registrationId}",
					"https://api.github.com/user",
					"https://github.com/login/oauth/access_token",
					IdTokenClaimNames.SUB,
					"",
					Collections.emptyMap()
				);
	}
	
	@Before
	public void setUpClientDetails() {
		clientDetails = new BguClientDetails(
				System.getenv("GH_API_CLIENT_ID"),
				"github-api",
				"Github",
				System.getenv("GH_API_CLIENT_SECRET"),
				"resource_a",
				"openid,profile,email,user,repo",
				"client_credentials,password,authorization_code",
				"http://localhost:8080",
				"ROLE_CLIENT_API",
				10_000,
				60_000,
				Collections.emptyMap()
				);
	}
	
	@Test
	public void clientRegistrationRepo_ShouldAccuratelyPull_BguClientRegistration() {
		/*
		 * Given a valid client
		 */
		assertNotNull(template.save(appClient, "bgu_client_registration"));
		assertNotNull(template.save(clientDetails, "bgu_client_details"));
		
		/*
		 * When we find a ClientRegistration by registration id
		 */
		final ClientRegistration registration = repo.findByRegistrationId("github-api");
		assertEquals("Github", registration.getClientName());
		assertTrue(StringUtils.hasText(registration.getClientId()));
		assertTrue(StringUtils.hasText(registration.getClientSecret()));
		assertTrue(registration.getScopes().contains("openid"));
		assertTrue(registration.getScopes().contains("profile"));
		assertTrue(registration.getScopes().contains("email"));
		assertTrue(registration.getScopes().contains("user"));
		assertTrue(registration.getScopes().contains("repo"));
		assertEquals(ClientAuthenticationMethod.POST, registration.getClientAuthenticationMethod());
		
	}
}
