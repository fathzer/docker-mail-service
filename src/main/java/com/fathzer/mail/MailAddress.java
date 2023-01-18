package com.fathzer.mail;

import org.apache.commons.validator.routines.EmailValidator;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

public class MailAddress {
	private InternetAddress address;
	
	public MailAddress(String address) {
		if (!EmailValidator.getInstance().isValid(address)) {
			throw new IllegalArgumentException(address+" is not a valid email address");
		}
		try {
			this.address = new InternetAddress(address);
		} catch (AddressException e) {
			throw new IllegalArgumentException(e);
		}
	}

	InternetAddress getAddress() {
		return address;
	}
}
