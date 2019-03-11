package org.bgu.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bgu.config.BaseMongoTest;
import org.bgu.model.interfaces.BguUserDetails;
import org.bgu.service.oauth.interfaces.BguUserDetailsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

public class BguUserDetailsServiceTest extends BaseMongoTest {

	@Autowired
	private BguUserDetailsService userDetailsService;
	
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
		assertEquals("password", userDetails.getPassword());
		assertEquals(Arrays.asList("ROLE_TEST"), userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		assertTrue(userDetails.isEnabled());
		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
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
		assertEquals("password", userDetails.getPassword());
		assertEquals(Arrays.asList("ROLE_TEST"), userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		assertTrue(userDetails.isEnabled());
		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
	}
}
