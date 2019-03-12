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

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoPropertiesTest {

	@Autowired
	private MongoProperties mongoProps;
	
	@Test
	public void mongoProps_ShouldGetUrl_Properly() {
		assertTrue(StringUtils.hasText(mongoProps.getUrl()));
	}
	
	@Test
	public void mongoProps_ShouldGetUsername_Properly() {
		assertTrue(StringUtils.hasText(mongoProps.getUsername()));
	}
	
	@Test
	public void mongoProps_ShouldGetPassword_Properly() {
		assertTrue(StringUtils.hasText(new String(mongoProps.getPassword())));
	}
	
	@Test
	public void mongoProps_ShouldGetMappingBasePackages_Properly() {
		assertTrue(!mongoProps.getMappingBasePackages().isEmpty());
	}
	
	@Test
	public void mongoProps_ShouldGetDatabaseName_Properly() {
		assertTrue(StringUtils.hasText(mongoProps.getDatabase()));
	}
	
	@Test
	public void mongoProps_ShouldGetRegisterMBeans_Properly() {
		assertTrue(mongoProps.isRegisterMbeans());		
	}
	
	@Test
	public void mongoProps_ShouldGetMaxConnectionsPerHost_Properly() {
		assertNotNull(new Integer(mongoProps.getConnectionsPerHost()));		
	}
	
	@Test
	public void mongoProps_ShouldGetConnectionTimeOut_Properly() {
		assertNotNull(new Integer(mongoProps.getConnectionTimeout()));
	}
	
	@Test
	public void mongoProps_ShouldGetConnectionIdleTime_Properly() {
		assertNotNull(new Integer(mongoProps.getConnectionIdleTime()));
	}
	
	@Test
	public void mongoProps_ShouldGetConnectionLifeTime_Properly() {
		assertNotNull(new Integer(mongoProps.getConnectionLifeTime()));
	}
	
}
