package org.bgu.config.oauth;

import java.util.Arrays;

import org.bgu.service.oauth.ApplicationTokenEnhancer;
import org.bgu.service.oauth.ApplicationTokenStore;
import org.bgu.service.oauth.BguClientDetailsService;
import org.bgu.service.oauth.interfaces.BguUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

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
	private ApplicationTokenStore tokenStore;
	
	@Autowired
	private ApplicationTokenEnhancer tokenEnhancer;
	
	@Autowired
	private JwtAccessTokenConverter jwtAccessTokenConverter;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
			.withClientDetails(clientDetailsService);
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain chain = new TokenEnhancerChain();
		chain.setTokenEnhancers(Arrays.asList(tokenEnhancer, jwtAccessTokenConverter));
		endpoints
			.authenticationManager(authManager)
			.userDetailsService(userDetailsService)
			.tokenStore(tokenStore)
			.tokenServices(tokenServices())
			.tokenEnhancer(chain)
			.setClientDetailsService(clientDetailsService);
	}
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security
			.tokenKeyAccess("permitAll()")
			.checkTokenAccess("isAuthenticated()")
			.allowFormAuthenticationForClients();
	}
	
	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		TokenEnhancerChain chain = new TokenEnhancerChain();
		chain.setTokenEnhancers(Arrays.asList(tokenEnhancer, jwtAccessTokenConverter));
		DefaultTokenServices services = new DefaultTokenServices();
		services.setAuthenticationManager(authManager);
		services.setTokenStore(tokenStore);
		services.setTokenEnhancer(chain);
		services.setSupportRefreshToken(true);
		services.setClientDetailsService(clientDetailsService);
		return services;
	}
}
