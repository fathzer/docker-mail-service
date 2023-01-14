package com.fathzer.mail;

import java.util.List;

import javax.mail.MessagingException;

public interface Mailer {
	void sendMail(List<String> recipients, String subject, String message) throws MessagingException;
}
