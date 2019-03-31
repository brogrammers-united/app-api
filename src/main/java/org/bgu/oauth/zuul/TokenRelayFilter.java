package org.bgu.oauth.zuul;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.cloud.security.oauth2.proxy.ProxyAuthenticationProperties;
import org.springframework.cloud.security.oauth2.proxy.ProxyAuthenticationProperties.Route;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class TokenRelayFilter extends ZuulFilter {

	private final Logger logger = LogManager.getLogger(getClass());
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String TOKEN_TYPE = "TOKEN_TYPE";
	private final Map<String, Route> routes;
	private final OAuth2RestOperations restTemplate;
	
	public TokenRelayFilter(ProxyAuthenticationProperties props, OAuth2RestOperations restTemplate) {
		this.routes = props.getRoutes();
		this.restTemplate = restTemplate;
	}
	
	@Override
	public boolean shouldFilter() {
		logger.info("{} request coming to {}", RequestContext.getCurrentContext().getRequest().getMethod(), RequestContext.getCurrentContext().getRequest().getRequestURI());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof OAuth2Authentication) {
			Object details = auth.getDetails();
			if (details instanceof OAuth2AuthenticationDetails) {
				OAuth2AuthenticationDetails oauth = (OAuth2AuthenticationDetails) details;
				RequestContext context = RequestContext.getCurrentContext();
				if (context.containsKey("proxy")) {
					String id = (String) context.get("proxy");
					if (routes.containsKey(id)) {
						if (!Route.Scheme.OAUTH2.matches(routes.get(id).getScheme()))
							return false;
					}
				}
				context.set(ACCESS_TOKEN, oauth.getTokenValue());
				context.set(TOKEN_TYPE, oauth.getTokenType() == null ? "Bearer" : oauth.getTokenType());
				return true;
			}
		}
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();
		logger.info("{} request going to {}", context.getRequest().getMethod(), context.getRequest().getRequestURI());

		context.addZuulRequestHeader("Authorization", context.get(TOKEN_TYPE) + " " + getAccessToken(context));
		return null;
	}

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 10;
	}
	
	private String getAccessToken(RequestContext ctx) {
		String value = (String) ctx.get(ACCESS_TOKEN);
		if (restTemplate != null) {
			// In case it needs to be refreshed
			OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
			if (restTemplate.getResource().getClientId().equals(auth.getOAuth2Request().getClientId())) {
				try {
					value = restTemplate.getAccessToken().getValue();
				} catch (Exception e) {
					// Quite possibly a UserRedirectRequiredException, but the caller
					// probably doesn't know how to handle it, otherwise they wouldn't be
					// using this filter, so we rethrow as an authentication exception
					ctx.set("error.status_code", HttpServletResponse.SC_UNAUTHORIZED);
					throw new BadCredentialsException("Cannot obtain valid access token");
				}
			}
		}
		return value;
	}

}
