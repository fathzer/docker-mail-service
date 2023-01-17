package com.fathzer.mail;

import java.util.List;

import javax.mail.MessagingException;

public interface Mailer {
	static final String TEXT_UTF8 = "text/plain; charset=UTF-8";
	
	void sendMail(List<String> recipients, String subject, String message, String mimeType) throws MessagingException;
}
