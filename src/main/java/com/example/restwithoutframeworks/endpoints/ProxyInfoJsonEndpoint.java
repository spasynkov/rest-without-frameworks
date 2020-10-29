package com.example.restwithoutframeworks.endpoints;

import org.json.JSONObject;

import com.example.restwithoutframeworks.Request;
import com.example.restwithoutframeworks.Response;

import lombok.Builder;
import lombok.Value;

public class ProxyInfoJsonEndpoint extends AbstractEndpoint {
	public ProxyInfoJsonEndpoint(String url) {
		super(url);
	}

	@Override
	public Response processGet(Request request) {
		ProxyData proxyData = new ProxyData("proxy-server.com", 12345, "admin", "qwerty");
		JSONObject json = new JSONObject(proxyData);
		return new Response(json.toString(), 200);
	}

	@Value
	@Builder
	public static class ProxyData {
		String domain;
		int port;
		String login;
		String password;
	}
}
