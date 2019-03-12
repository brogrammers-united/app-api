package org.bgu.properties;

import static org.junit.Assert.assertTrue;

import org.bgu.config.properties.MailProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailPropertiesTest {

	@Autowired
	private MailProperties mailProps;
	
	@Test
	public void usernameShouldHaveText() {
		assertTrue(StringUtils.hasText(mailProps.getUsername()));
	}
	
	@Test
	public void passwordShouldHaveText() {
		assertTrue(StringUtils.hasText(mailProps.getPassword()));
	}
	
	@Test
	public void subjectLineShouldHaveText() {
		assertTrue(StringUtils.hasText(mailProps.getSubjectLine()));
	}
	
	@Test
	public void fromAddressShouldHaveText() {
		assertTrue(StringUtils.hasText(mailProps.getFromAddress()));
	}
}
