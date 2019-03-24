package org.bgu.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.bgu.config.BaseMongoTest;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.model.oauth.BguUser;
import org.bgu.oauth.service.BguUserDetailsServiceImpl;
import org.bgu.oauth.service.interfaces.BguUserDetailsService;
import org.bgu.repository.impl.ApplicationUserRepositoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BguUserDetailsServiceTest extends BaseMongoTest {

	private BguUserDetailsService userDetailsService;
	
	@Autowired
	private PasswordEncoder encoder;
	
	private BguUser user;
	
	@Before
	public void setUpUserDetailsService() {
		this.userDetailsService = new BguUserDetailsServiceImpl(new ApplicationUserRepositoryImpl(template));
	}
	
	@Before
	public void setUpUser() {
		user = 	new BguUser(
				"test_user",
				"ROLE_TEST",
				"test@test.com",
				true,
				true,
				true,
				true,
				Collections.emptyMap(),
				"some_access_token"
			);
	}
	
	@Test
	public void userDetailsService_ShouldLoadUser_ByEmail() {
		/*
		 * Given an existing valid user
		 */
		template.save(user, "bgu_user");
		
		/*
		 * When the UserDetailsService loads by email
		 */
		final BguUserDetails userDetails = userDetailsService.loadUserByEmail("test@test.com");
		
		/*
		 * Then the user should be retrieved properly
		 */
		assertEquals("test_user", userDetails.getUsername());
		assertTrue(encoder.matches("password", userDetails.getPassword()));
		assertEquals(Arrays.asList("ROLE_TEST"), userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		assertTrue(userDetails.isEnabled());
		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
		
		// Clean up
		template.remove(user, "bgu_user");
	}
	
	@Test
	public void userDetailsService_ShouldLoadUser_ByUsername() {
		/*
		 * Given an existing valid user
		 */
		template.save(user, "bgu_user");
		
		/*
		 * When the UserDetailsService loads by username
		 */
		final BguUserDetails userDetails = userDetailsService.loadUserByUsername("test_user");
		
		/*
		 * Then the user should be retrieved properly
		 */
		assertEquals("test@test.com", userDetails.getEmail());
		assertTrue(encoder.matches("password", userDetails.getPassword()));
		assertEquals(Arrays.asList("ROLE_TEST"), userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		assertTrue(userDetails.isEnabled());
		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
		
		// Clean up
		template.remove(user, "bgu_user");
	}
}
