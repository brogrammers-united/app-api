package org.bgu.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.bgu.config.properties.MongoProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoPropertiesTest {

	@Autowired
	private MongoProperties mongoProps;
	
	@Test
	public void mongoProps_ShouldGetUrl_Properly() {
		assertEquals("127.0.0.1", mongoProps.getUrl());
	}
	
	@Test
	public void mongoProps_ShouldGetUsername_Properly() {
		assertEquals("test_admin",mongoProps.getUsername());		
	}
	
	@Test
	public void mongoProps_ShouldGetPassword_Properly() {
		assertEquals("Password123!", new String(mongoProps.getPassword()));
	}
	
	@Test
	public void mongoProps_ShouldGetMappingBasePackages_Properly() {
		assertEquals(Arrays.asList("org.bgu.model.oauth"), mongoProps.getMappingBasePackages());
	}
	
	@Test
	public void mongoProps_ShouldGetDatabaseName_Properly() {
		assertEquals("gh_oauth2_test", mongoProps.getDatabase());
	}
	
	@Test
	public void mongoProps_ShouldGetRegisterMBeans_Properly() {
		assertTrue(mongoProps.isRegisterMbeans());		
	}
	
	@Test
	public void mongoProps_ShouldGetMaxConnectionsPerHost_Properly() {
		assertEquals(100, mongoProps.getConnectionsPerHost());		
	}
	
	@Test
	public void mongoProps_ShouldGetConnectionTimeOut_Properly() {
		assertEquals(10, mongoProps.getConnectionTimeout());
	}
	
	@Test
	public void mongoProps_ShouldGetConnectionIdleTime_Properly() {
		assertEquals(10, mongoProps.getConnectionIdleTime());
	}
	
	@Test
	public void mongoProps_ShouldGetConnectionLifeTime_Properly() {
		assertEquals(10, mongoProps.getConnectionLifeTime());
	}
	
}
