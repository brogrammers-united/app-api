package org.bgu.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgu.config.LoggerLevel;
import org.bgu.exception.FailedAuthenticationAttemptException;
import org.bgu.exception.InvalidAuthenticationRequestFormatException;
import org.bgu.model.dto.AuthenticationRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BguAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	
	private final Logger logger = LogManager.getLogger(getClass());
	private final AuthenticationManager authManager;
	private final ObjectMapper mapper;
	
	protected BguAuthenticationFilter(final AuthenticationManager authManager, final ObjectMapper mapper) {
		super(new AntPathRequestMatcher("/login", "POST"));
		this.authManager = authManager;
		this.mapper = mapper;
		super.setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		return authManager.authenticate(getAuthRequest(request));
	}
	
	protected UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest req) {
		/*
		 * Incoming request must have a content type of application/json;charset=utf-8
		 */
		if (!StringUtils.hasText(req.getHeader("Content-Type")) || !req.getHeader("Content-Type").equalsIgnoreCase(MediaType.APPLICATION_JSON_UTF8_VALUE))
			throw new FailedAuthenticationAttemptException("Invalid content type");
		AuthenticationRequest request = null;
		try {
			request = mapper.readValue(req.getInputStream(), AuthenticationRequest.class);
		} catch (IOException e) {
			throw new InvalidAuthenticationRequestFormatException();
		} 
		if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword()))
			throw new InvalidAuthenticationRequestFormatException();
		logger.log(LoggerLevel.AUTHENTICATION, "{} attempting authentication at {}", request.getUsername(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));
		return new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
	}
}
