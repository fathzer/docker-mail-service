package com.fathzer.mailservice;

import java.util.LinkedList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name="Reply", description = "A web service's reply", requiredProperties = {"messages"},
example = "{\"messages\":[\"Mail was sent\"]}")
public class Reply {
	private List<String> messages;
	
	Reply() {
		this.messages = new LinkedList<>();
	}
	
	void addMessage(String message) {
		this.messages.add(message);
	}
}
