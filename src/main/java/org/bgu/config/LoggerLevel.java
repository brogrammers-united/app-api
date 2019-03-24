package org.bgu.config;

import org.apache.logging.log4j.Level;

public class LoggerLevel {

	public static final Level SECURITY = Level.forName("SECURITY", 150);
	public static final Level AUTHENTICATION = Level.forName("AUTHENTICATION", 151);
	public static final Level OAUTH = Level.forName("OAUTH", 152);
	
	// Restrict Instantiation
	private LoggerLevel() {}
}
