package org.bgu.properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.bgu.config.properties.MongoProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoPropertiesTest {

	@Autowired
	private MongoProperties mongoProperties;
	
	@Test
	public void mongoProps_ShouldGetUrl_Properly() {
		assertTrue(StringUtils.hasText(mongoProperties.getUrl()));
	}

	@Test
	public void mongoProps_ShouldGetPort_Properly() {
		assertTrue(mongoProperties.getPort() > 0);
	}
	
	@Test
	public void mongoProps_ShouldGetUsername_Properly() {
		assertTrue(StringUtils.hasText(mongoProperties.getUsername()));
	}
	
	@Test
	public void mongoProps_ShouldGetPassword_Properly() {
		assertTrue(mongoProperties.getPassword().length > 0);
	}
	
	@Test
	public void mongoProps_ShouldGetMappingBasePackages_Properly() {
		assertTrue(!mongoProperties.getMappingBasePackages().isEmpty());
	}
	
	@Test
	public void mongoProps_ShouldGetDatabaseName_Properly() {
		assertTrue(StringUtils.hasText(mongoProperties.getDatabase()));
	}
	
	@Test
	public void mongoProps_ShouldGetRegisterMBeans_Properly() {
		assertTrue(mongoProperties.isRegisterMbeans());
	}
	
	@Test
	public void mongoProps_ShouldGetMaxConnectionsPerHost_Properly() {
		assertNotNull(new Integer(mongoProperties.getConnectionsPerHost()));
	}
	
	@Test
	public void mongoProps_ShouldGetConnectionTimeOut_Properly() {
		assertNotNull(new Integer(mongoProperties.getConnectionTimeout()));
	}
	
	@Test
	public void mongoProps_ShouldGetConnectionIdleTime_Properly() {
		assertNotNull(new Integer(mongoProperties.getConnectionIdleTime()));
	}
	
	@Test
	public void mongoProps_ShouldGetConnectionLifeTime_Properly() {
		assertNotNull(new Integer(mongoProperties.getConnectionLifeTime()));
	}
	
}
