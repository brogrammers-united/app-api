package org.bgu.config;

import org.bgu.oauth.service.interfaces.BguUserDetailsService;
import org.bgu.security.HttpCookieOAuth2AuthorizationRequestRepository;
import org.bgu.security.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private BguUserDetailsService userDetailsService;
	
	@Autowired
	private HttpCookieOAuth2AuthorizationRequestRepository requestRepo;
	
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	
	@Autowired
	private OAuth2AuthenticationSuccessHandler successHandler;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService);
	}
	
	@Override
	public BguUserDetailsService userDetailsServiceBean() throws Exception {
		return userDetailsService;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf()
				.disable()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.and()
			.httpBasic()
				.disable()
			.formLogin()
				.disable()
			.authorizeRequests()

				.mvcMatchers(HttpMethod.GET, "/login/**").permitAll()
				.mvcMatchers("/webjars/**").permitAll()
				.mvcMatchers("/img/**").permitAll()
				.anyRequest().authenticated()
				.and()
			.oauth2Login()
				.loginPage("/login")
				.successHandler(successHandler)
				.clientRegistrationRepository(clientRegistrationRepository)
				.authorizationEndpoint()
					.baseUri("/oauth2/authorize")
					.authorizationRequestRepository(requestRepo)
					.and()
				.redirectionEndpoint()
					.baseUri("/")
					.and()
				.and()
			.oauth2Client()
				.authorizationCodeGrant()
					.accessTokenResponseClient(new DefaultAuthorizationCodeTokenResponseClient());
			
	}
	
}
