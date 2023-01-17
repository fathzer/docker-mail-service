package com.fathzer.mailservice;

import java.util.Set;

import com.fathzer.mail.Mailer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MailSettings {
	private Mailer mailer;
	private Set<String> authorizedDest;
	
	public boolean isAuthorized(String dest) {
		return authorizedDest==null || authorizedDest.contains(dest.toLowerCase());
	}
}
