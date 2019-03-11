package org.bgu.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.bgu.config.BaseMongoTest;
import org.bgu.model.oauth.ApplicationClientDetails;
import org.bgu.service.oauth.BguClientDetailsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BguClientDetailsTest extends BaseMongoTest {
	
	@Autowired
	private PasswordEncoder encoder;
	
	private final BguClientDetailsService clientDetailsService = new BguClientDetailsService(clientDetailsRepo);
	
	@Test
	public void BguClientDetailsService_ShouldGetClientBy_RegistrationId_Properly() {
		final ApplicationClientDetails clientDetails = new ApplicationClientDetails(
				"bgu-cli-client",
				"cli",
				"BGU CLI Application",
				encoder.encode("bgu-cli-secret"),
				"resource_a,resource_b",
				"cli:admin",
				"client_credentials,password",
				"http://localhost:8080/home",
				"ROLE_CLI_ADMIN",
				600,
				3600,
				Collections.emptyMap()
			);
		
		assertNotNull(clientDetailsRepo.save(clientDetails));
		
		final ApplicationClientDetails details = clientDetailsService.loadClientByClientId("bgu-cli-client");
		assertNotNull(details);
		assertTrue(encoder.matches("bgu-cli-secret", details.getClientSecret()));
		assertEquals("BGU CLI Application", details.getClientName());
		assertEquals("cli", details.getRegistrationId());
		assertEquals(Arrays.asList("cli:admin").stream().collect(Collectors.toSet()), details.getScope());
		assertEquals(Arrays.asList("ROLE_CLI_ADMIN"), details.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		assertEquals(Arrays.asList("client_credentials", "password").stream().collect(Collectors.toSet()), details.getAuthorizedGrantTypes());
	}
}
