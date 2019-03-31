package org.bgu.oauth.zuul;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.exception.InvalidAuthenticationRequestFormatException;
import org.bgu.oauth.service.BguTokenStore;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Service
public class ZuulTokenAuthenticationFilter extends ZuulFilter {

	private final Logger logger = LogManager.getLogger(getClass());
	private final BguTokenStore tokenStore;
	
	public ZuulTokenAuthenticationFilter(BguTokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}
	
	@Override
	public boolean shouldFilter() {
		logger.info("{} request coming to {}", RequestContext.getCurrentContext().getRequest().getMethod(), RequestContext.getCurrentContext().getRequest().getRequestURI());
		return requestHasToken(RequestContext.getCurrentContext());
	}

	@Override
	public Object run() throws ZuulException {
		final String token = getToken(RequestContext.getCurrentContext());
		if (StringUtils.hasText(token)) {
			OAuth2Authentication authentication = tokenStore.readAuthentication(token);
			logger.info("User associated with token: {}", authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		return null;
	}

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 9;
	}
	
	private boolean requestHasToken(RequestContext context) {
		return context.getRequest().getHeader("Authorization") != null ||
				context.getZuulRequestHeaders().containsKey("Authorization");
	}

	private String getToken(RequestContext context) {
		if (context.getRequest().getHeader("Authorization") != null && context.getRequest().getHeader("Authorization").startsWith("Bearer ")) {
			return context.getRequest().getHeader("Authorization").replace("Bearer ", "");
		} 
		if (context.getZuulRequestHeaders().containsKey("Authorization") && context.getZuulRequestHeaders().get("Authorization").startsWith("Bearer ")) {
			return context.getZuulRequestHeaders().get("Authorization").replace("Bearer ", "");
		}
		throw new InvalidAuthenticationRequestFormatException("Failed to locate token");
	}
}