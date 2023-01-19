package com.fathzer.mail;

import java.io.IOException;
import java.util.List;

public interface Mailer {
	void sendMail(List<MailAddress> recipients, String subject, String message, MimeType mimeType) throws IOException;
}
