package com.fathzer.mail;

public class MimeType {
	public static final MimeType TEXT = new MimeType("text/plain");
	public static final MimeType HTML = new MimeType("text/html");
	
	private final String asString;

	public MimeType(String string) {
		this.asString = string;
	}

	@Override
	public String toString() {
		return asString;
	}
}
