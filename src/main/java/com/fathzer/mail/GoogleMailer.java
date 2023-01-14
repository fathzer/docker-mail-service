package com.fathzer.mail;

public class GoogleMailer extends DefaultMailer {
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";

	public GoogleMailer(String userAddress, String userPwd) {
		super(SMTP_HOST_NAME, userAddress, userPwd);
	}
}
