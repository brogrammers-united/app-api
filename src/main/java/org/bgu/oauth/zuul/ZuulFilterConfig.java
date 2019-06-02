package org.bgu.oauth.zuul;

import org.bgu.oauth.service.BguTokenStore;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author William Gentry
 */
@EnableZuulProxy
@Configuration
public class ZuulFilterConfig {

    @Bean
    public ZuulTokenAuthenticationFilter zuulTokenAuthenticationFilter(BguTokenStore bguTokenStore) {
        return new ZuulTokenAuthenticationFilter(bguTokenStore);
    }

}
