package com.fathzer.mail;

import java.util.Properties;
import java.util.function.Consumer;

public enum Encryption {
	NONE(25, p -> {}),
	TLS(587, p -> p.put("mail.smtp.starttls.enable", "true")),
	SSL(465, p -> {
		p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.put("mail.smtps.ssl.checkserveridentity","true");
	});

	private int defaultPort;
	private Consumer<Properties> propBuilder;
	
	private Encryption(int port, Consumer<Properties> propBuilder) {
		this.defaultPort = port;
		this.propBuilder = propBuilder;
	}

	public int getDefaultPort() {
		return defaultPort;
	}
	
	void apply(Properties properties) {
		propBuilder.accept(properties);
	}
}
