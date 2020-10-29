package com.example.restwithoutframeworks.endpoints;

import java.util.Collections;

import com.example.restwithoutframeworks.Request;
import com.example.restwithoutframeworks.Response;

public class HelloWithParamsEndpoint extends AbstractEndpoint {
	public HelloWithParamsEndpoint(String url) {
		super(url);
	}

	@Override
	public Response processGet(Request request) {
		String noNameText = "Anonymous";
		String name = request.getParams().getOrDefault("name", Collections.singletonList(noNameText)).stream().findFirst().orElse(noNameText);
		String respText = String.format("Hello %s!", name);
		return new Response(respText, 200);
	}
}
