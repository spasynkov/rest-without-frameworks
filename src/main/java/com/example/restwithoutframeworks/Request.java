package com.example.restwithoutframeworks;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Request {
	private String url;
	private RequestMethod method;
	private Map<String, List<String>> params;
	private String body;

	public enum RequestMethod {
		GET,
		POST,
		PUT,
		DELETE
	}
}
