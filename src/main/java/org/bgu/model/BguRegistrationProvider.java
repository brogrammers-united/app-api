package org.bgu.model;

public enum BguRegistrationProvider implements RegistrationProvider {
	
	CLI("bgu-cli", "cli"), WEB_APP("bgu-web-app", "web-app"), GITHUB("github", "github");
	
	private final String registrationId;
	private final String provider;
	
	BguRegistrationProvider(final String registrationId, final String provider) {
		this.registrationId = registrationId;
		this.provider = provider;
	}
	
	public String getRegistrationId() {
		return registrationId;
	}
	
	public String getProvider() {
		return provider;
	}
}
