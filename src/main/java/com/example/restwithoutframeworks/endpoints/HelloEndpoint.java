package com.example.restwithoutframeworks.endpoints;

import com.example.restwithoutframeworks.Request;
import com.example.restwithoutframeworks.Response;

public class HelloEndpoint extends AbstractEndpoint {
	public HelloEndpoint(String url) {
		super(url);
	}

	@Override
	public Response processGet(Request request) {
		return new Response("Hello", 200);
	}
}
