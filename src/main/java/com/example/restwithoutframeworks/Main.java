package com.example.restwithoutframeworks;

import com.example.restwithoutframeworks.endpoints.HelloEndpoint;
import com.example.restwithoutframeworks.endpoints.HelloWithParamsEndpoint;
import com.example.restwithoutframeworks.endpoints.ProxyInfoJsonEndpoint;
import com.sun.net.httpserver.BasicAuthenticator;

public class Main {
	public static void main(String[] args) throws Exception {
		BasicAuthenticator authenticator = new BasicAuthenticator("myrealm") {
			@Override
			public boolean checkCredentials(String user, String pwd) {
				return user.equals("admin") && pwd.equals("qwerty");
			}
		};

		RestServer server = RestServer.builder()
				.setPort(80)
				.setUrl("/rest")
				//.setAuth(authenticator)     // user: admin     pass:qwerty
				.addEndpoint(new HelloEndpoint("/hello"))
				.addEndpoint(new HelloWithParamsEndpoint("/hello/withParams"))
				.addEndpoint(new ProxyInfoJsonEndpoint("/proxy"))
				.build();

		Thread.sleep(60 * 1000);
		System.out.println("Time is over. Stopping server...");
		server.stop();
		System.out.println("Stopped.");
	}
}
