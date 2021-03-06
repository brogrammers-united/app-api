package org.bgu.config;

import org.bgu.security.HttpCookieOAuth2AuthorizationRequestRepository;
import org.bgu.service.oauth.interfaces.BguUserDetailsService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private BguUserDetailsService userDetailsService;
	
	@Autowired
	private HttpCookieOAuth2AuthorizationRequestRepository requestRepo;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf()
				.ignoringRequestMatchers(new AntPathRequestMatcher("/oauth/token", HttpMethod.POST.toString()),
										 new AntPathRequestMatcher("/login", HttpMethod.POST.toString()),
										 new AntPathRequestMatcher("/register", HttpMethod.POST.toString()),
										 new AntPathRequestMatcher("/oauth2/*", HttpMethod.POST.toString()))
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.and()
			.httpBasic()
				.disable()
			.formLogin()
				.disable()
			.authorizeRequests()
				.mvcMatchers(HttpMethod.POST, "/oauth/token").permitAll()
				.mvcMatchers(HttpMethod.POST, "/login").permitAll()
				.mvcMatchers(HttpMethod.POST, "/register").permitAll()
				.mvcMatchers(HttpMethod.POST, "/oauth2/*").permitAll()
				.and()
			.oauth2Login()
				.authorizationEndpoint()
					.baseUri("/oauth2/authorize")
					.authorizationRequestRepository(requestRepo)
					.and()
				.redirectionEndpoint()
					.baseUri("/")
					.and();
	}
	
}
