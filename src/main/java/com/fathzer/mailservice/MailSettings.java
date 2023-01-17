package com.fathzer.mailservice;

import com.fathzer.mail.Mailer;

public record MailSettings(Mailer mailer, AddressValidator destValidator) {}
