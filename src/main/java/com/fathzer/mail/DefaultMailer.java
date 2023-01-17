package com.fathzer.mail;

import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DefaultMailer implements Mailer {
	private final Session session;
	private Address sender;
	private Address[] replyTo;

	public DefaultMailer(Session session, String sender) {
		this.session = session;
		this.sender = toAddress(sender);
	}
	
	public void setDebug(boolean debug) {
		session.setDebug(debug);
	}
	
	public void setSender(String sender) {
		this.sender = toAddress(sender);
	}
	
	public void setReplyTo(List<String> replyTo) {
		this.replyTo = replyTo==null?null:toAddresses(replyTo);
	}

	@Override
	public void sendMail(List<String> recipients, String subject, String message, String mimeType) throws MessagingException {

		final Message msg = new MimeMessage(session);
		msg.setFrom(sender);
		if (replyTo!=null) {
			msg.setReplyTo(replyTo);
		}
		msg.setRecipients(Message.RecipientType.TO, toAddresses(recipients));
		msg.setSubject(subject);
		msg.setContent(message, mimeType);

		// Setting the Subject and Content Type
		Transport.send(msg);
	}
	
	private Address toAddress(String addr) {
		try {
			return new InternetAddress(addr);
		} catch (AddressException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private Address[] toAddresses(List<String> addr) {
		return addr.stream().map(this::toAddress).toArray(Address[]::new);
	}

}
