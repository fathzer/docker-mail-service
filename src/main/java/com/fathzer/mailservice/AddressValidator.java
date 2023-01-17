package com.fathzer.mailservice;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.EmailValidator;

import lombok.Getter;

@Getter
public class AddressValidator implements Predicate<String> {
	private final Set<String> authorized;
	
	AddressValidator() {
		this.authorized = null;
	}

	AddressValidator(String adresses) {
		final EmailValidator validator = EmailValidator.getInstance();
		authorized = Arrays.stream(adresses.split(",")).map(s-> {
				final String candidate = s.trim().toLowerCase();
				if (!validator.isValid(candidate)) {
					throw new IllegalArgumentException(candidate+" is not a valid email address");
				}
				return candidate;
			}).collect(Collectors.toSet());
	}
	
	@Override
	public boolean test(String dest) {
		return authorized==null || authorized.contains(dest.toLowerCase());
	}
}
