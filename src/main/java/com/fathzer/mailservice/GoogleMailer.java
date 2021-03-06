package com.fathzer.mailservice;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GoogleMailer {
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_PORT = "587";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	private String user;
	private String password;
	private boolean debug;

	public GoogleMailer(String userAddress, String userPwd) {
		this.user = userAddress;
		this.password = userPwd;
		this.debug = false;
	}

	public void sendMail(List<String> recipients, String subject, String message) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", Boolean.toString(debug));
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		session.setDebug(debug);

		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(user);
		msg.setFrom(addressFrom);
		msg.setSubject(subject);
		msg.setContent(message, "text/plain; charset=UTF-8");

		InternetAddress[] addressTo = new InternetAddress[recipients.size()];
		int i=0;
		for (String recipient : recipients) {
			addressTo[i] = new InternetAddress(recipient);
			i++;
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		Transport.send(msg);
	}
}
