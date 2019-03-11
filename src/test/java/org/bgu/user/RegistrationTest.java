package org.bgu.user;

import static org.junit.Assert.assertTrue;

import org.bgu.model.PersonalInformation;
import org.bgu.model.Phone;
import org.bgu.model.PhoneType;
import org.bgu.model.RegistrationForm;
import org.bgu.model.dto.RegistrationResponseDto;
import org.bgu.service.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RegistrationTest {

	@Autowired
	private RegistrationService registrationService;
	
	private RegistrationForm form;
	private Phone phone;
	private PersonalInformation info;
	
	@Before
	public void setUpRegistrationForm() {
		phone = new Phone("test_user", "404-867-5309", PhoneType.HOME);
		info = new PersonalInformation("test_user", "Test", "User", "01-01-1990");
		form = new RegistrationForm("test_user", "password", "password", "test@test.com", phone, info);
	}
	
	@Test
	public void registrationService_ShouldProperlyRegisterUserWith_ValidRegistrationForm() {
		RegistrationResponseDto response = registrationService.attemptRegistration(form);
		assertTrue(StringUtils.hasText(response.getMessage()));
	}
}
