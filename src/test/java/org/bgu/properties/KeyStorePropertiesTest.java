package org.bgu.properties;

import static org.junit.Assert.assertTrue;

import org.bgu.config.properties.KeyStoreProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KeyStorePropertiesTest {

	@Autowired
	private KeyStoreProperties keyStoreProps;
	
	@Test
	public void keyStoreAliasShouldHaveText() {
		assertTrue(StringUtils.hasText(keyStoreProps.getAlias()));
	}
	
	@Test
	public void keyStoreFileShouldHaveText() {
		assertTrue(StringUtils.hasText(keyStoreProps.getFileName()));
	}
	
	@Test
	public void keyStoreTypeShouldHaveText() {
		assertTrue(StringUtils.hasText(keyStoreProps.getType()));
	}
	
	@Test
	public void keyStorePasswordShouldHaveText() {
		assertTrue(StringUtils.hasText(new String(keyStoreProps.getPassword())));
	}
}
