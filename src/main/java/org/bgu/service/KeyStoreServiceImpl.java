package org.bgu.service;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KeyStoreServiceImpl implements KeyStoreService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final KeyPair keyPair;
	
	public KeyStoreServiceImpl() {
		try {
			KeyStore keyStore = KeyStore.getInstance("pkcs12");
			keyStore.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("authz_server.jks"), "Password123!".toCharArray());
			Key key = keyStore.getKey("authz_server", "Password123!".toCharArray());
			if (key instanceof PrivateKey) {
				// Get the certificate
				Certificate cert = keyStore.getCertificate("authz_server");
				
				// Get Public Key
				PublicKey pubKey = cert.getPublicKey();
				this.keyPair = new KeyPair(pubKey, (PrivateKey) key);
			} else {
				throw new IOException("Failed to read KeyStore");
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			logger.error("Failed to generate KeyPair: {}", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public KeyPair getKeyPair() {
		return this.keyPair;
	}
}
