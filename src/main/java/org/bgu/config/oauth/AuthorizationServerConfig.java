package org.bgu.config.oauth;

import java.util.ArrayList;
import java.util.List;

import org.bgu.oauth.service.BguClientDetailsService;
import org.bgu.oauth.service.BguClientRegistrationRepository;
import org.bgu.oauth.service.BguTokenStore;
import org.bgu.oauth.service.interfaces.BguUserDetailsService;
import org.bgu.security.HttpCookieOAuth2AuthorizationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private BguUserDetailsService userDetailsService;

	@Autowired
	private BguClientDetailsService clientDetailsService;

	@Autowired
	private BguTokenStore tokenStore;

	@Autowired
	private TokenEnhancer tokenEnhancerChain;
	
	@Autowired
	private BguClientRegistrationRepository clientRegistrationRepository;
	
	@Autowired
	private HttpCookieOAuth2AuthorizationRequestRepository requestRepo;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
	}
	
	@Bean
	public OAuth2LoginAuthenticationFilter oauth2LoginAuthenticationFilter() {
		OAuth2LoginAuthenticationFilter filter = new OAuth2LoginAuthenticationFilter(clientRegistrationRepository, new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository), "/oauth/login");
		filter.setAuthenticationManager(authManager);
		filter.setAuthorizationRequestRepository(requestRepo);
		return filter;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			.authenticationManager(authManager)
			.userDetailsService(userDetailsService)
			.tokenStore(tokenStore)
			.tokenServices(tokenServices())
			.tokenEnhancer(tokenEnhancerChain)
			.setClientDetailsService(clientDetailsService);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security
			.tokenKeyAccess("denyAll()")
			.checkTokenAccess("isAuthenticated()")
			.allowFormAuthenticationForClients();
	}

	@Bean
	@Primary
	public AuthorizationServerTokenServices tokenServices() {
		DefaultTokenServices services = new DefaultTokenServices();
		services.setAuthenticationManager(authManager);
		services.setTokenStore(tokenStore);
		services.setTokenEnhancer(tokenEnhancerChain);
		services.setSupportRefreshToken(true);
		services.setRefreshTokenValiditySeconds(60_000);
		services.setClientDetailsService(clientDetailsService);
		return services;
	}

	@Bean
	public AccessTokenProvider accessTokenProvider() {
		List<AccessTokenProvider> providers = new ArrayList<>();
		providers.add(new AuthorizationCodeAccessTokenProvider());
		providers.add(new ResourceOwnerPasswordAccessTokenProvider());
		providers.add(new ClientCredentialsAccessTokenProvider());
		return new AccessTokenProviderChain(providers);
	}

	@Bean
	public OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails details) {
		OAuth2RestTemplate template = new OAuth2RestTemplate(details, new DefaultOAuth2ClientContext());
		return template;
	}
	
}
