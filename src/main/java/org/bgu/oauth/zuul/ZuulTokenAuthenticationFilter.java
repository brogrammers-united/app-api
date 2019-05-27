package org.bgu.oauth.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.exception.InvalidRequestException;
import org.bgu.oauth.service.BguTokenStore;
import org.bgu.security.CookieUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
		final String token = getCookieToken(RequestContext.getCurrentContext());
		if (StringUtils.hasText(token)) {
			OAuth2Authentication authentication = tokenStore.readAuthentication(token);
			logger.info("User associated with token: {}", authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			logger.info("Security Context set! {}", SecurityContextHolder.getContext().getAuthentication());
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
		return CookieUtils.getCookie(context.getRequest(), "api_token").isPresent();
	}

	private String getCookieToken(RequestContext context) {
		return CookieUtils.getCookie(context.getRequest(), "api_token").orElseThrow(InvalidRequestException::new)
				.getValue();
	}
}
