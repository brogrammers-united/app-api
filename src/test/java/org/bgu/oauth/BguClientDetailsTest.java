package org.bgu.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bgu.config.BaseMongoTest;
import org.bgu.model.oauth.ApplicationClientDetails;
import org.bgu.service.oauth.BguClientDetailsService;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

public class BguClientDetailsTest extends BaseMongoTest {
	
	
	private final BguClientDetailsService clientDetailsService = new BguClientDetailsService(clientDetailsRepo);
	
	@Test
	public void BguClientDetailsService_ShouldGetClientBy_RegistrationId_Properly() {
		assertNotNull(clientDetailsRepo.save(clientDetails));
		
		final ApplicationClientDetails details = clientDetailsService.loadClientByClientId("bgu-cli-client");
		assertNotNull(details);
		assertEquals("bgu-cli-secret", details.getClientSecret());
		assertEquals("BGU CLI Application", details.getClientName());
		assertEquals("cli", details.getRegistrationId());
		assertEquals(Arrays.asList("cli:admin").stream().collect(Collectors.toSet()), details.getScope());
		assertEquals(Arrays.asList("ROLE_CLI_ADMIN"), details.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		assertEquals(Arrays.asList("client_credentials").stream().collect(Collectors.toSet()), details.getAuthorizedGrantTypes());
	}
}
