package com.fathzer.mailservice;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name="EMail", description = "An email.\n\nPlease note that it is mandatory to have one of the *to*, *cc* or *bcc* fields set.", requiredProperties = {"content"},
	example = "{\"to\":[\"me@gmail.com\"],\"content\":\"A mail from REST api\"}")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@JsonIgnoreProperties(ignoreUnknown = false)
public class EMailParams {
    @Schema(description = "The list of recipients.", example = "[\"me@gmail.com\"]")
	private List<String> to;
    @Schema(description = "The list of copy recipients.", example = "[\"copy@gmail.com\"]")
	private List<String> cc;
    @Schema(description = "The list of blind copy recipients.", example = "[\"blind@gmail.com\"]")
	private List<String> bcc;
	
    @Schema(description = "The mail's subject.", example = "EMail test")
    @JsonSetter(nulls = Nulls.SKIP)
	private String subject="";
    
    @Schema(description = "The mail's content.", example = "EMail test")
	private String content;
    
    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(description = "The encoding of mail's content.", example = "text/html", defaultValue = "text/plain", allowableValues = {"text/plain", "text/html"})
	private String encoding = "text/plain";
}
