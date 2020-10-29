package com.example.restwithoutframeworks;

import static java.net.URLDecoder.decode;
import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.regex.Pattern;

import com.example.restwithoutframeworks.endpoints.AbstractEndpoint;
import com.example.restwithoutframeworks.exceptions.EndpointNotFoundException;
import com.example.restwithoutframeworks.exceptions.RequestMethodNotImplementedException;
import com.example.restwithoutframeworks.exceptions.WrongRequestMethodException;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class RestServer {
	private String url;
	private HttpServer httpServer;
	private AbstractEndpoint[] endpoints;

	private RestServer() {} // restrict creating instances of server directly. use builder instead

	public void stop() {
		httpServer.stop(5);     // 5 sec - max time to wait till server stops. then it will drop all stucked connections
	}

	public void processRequest(HttpExchange exchange) {
		try {
			Request request = parseRequestData(exchange);

			AbstractEndpoint endpoint = findSuitableEndpointProcessor(request.getUrl());

			Response resp = null;
			try {
				switch (request.getMethod()) {
					case GET: {
						resp = endpoint.processGet(request);
						break;
					}
					case POST: {
						resp = endpoint.processPost(request);
						break;
					}
					case PUT: {
						resp = endpoint.processPut(request);
						break;
					}
					case DELETE: {
						resp = endpoint.processDelete(request);
						break;
					}
					default: throw new WrongRequestMethodException("Unknown request method");
				}

				validateResponse(resp);
				exchange.sendResponseHeaders(resp.getResponseCode(), resp.getText().getBytes().length);
			} catch (WrongRequestMethodException | RequestMethodNotImplementedException e) {
				sendResponseHeaders(exchange, 405);       // 405 Method Not Allowed
			}

			if (resp != null) {
				OutputStream output = exchange.getResponseBody();
				output.write(resp.getText().getBytes());
				output.flush();
			}

		} catch (EndpointNotFoundException e) {
			sendResponseHeaders(exchange, 404);     // 404 Not Found
		} catch (Throwable t) {
			sendResponseHeaders(exchange, 500);     // 500 Internal Server Error
		} finally {
			exchange.close();
		}
	}

	private void sendResponseHeaders(HttpExchange exchange, int responseCode) {
		try {
			exchange.sendResponseHeaders(responseCode, -1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private AbstractEndpoint findSuitableEndpointProcessor(String url) throws EndpointNotFoundException {
		url = url.replace(this.url, "");    // remove domain
		for (AbstractEndpoint endpoint : endpoints) {
			if (url.startsWith(endpoint.getUrl())) {
				return endpoint;
			}
		}

		throw new EndpointNotFoundException();
	}

	private Request parseRequestData(HttpExchange exchange) throws IOException {
		Map<String, List<String>> params = parseQueryParams(exchange.getRequestURI().getRawQuery());
		String body = parseRequestBody(exchange.getRequestBody());

		return new Request(exchange.getRequestURI().getPath(),
				Request.RequestMethod.valueOf(exchange.getRequestMethod()),
				params,
				body);
	}

	private Map<String, List<String>> parseQueryParams(String query) {
		if (query == null || "".equals(query)) {
			return Collections.emptyMap();
		}

		return Pattern.compile("&").splitAsStream(query)
				.map(s -> Arrays.copyOf(s.split("="), 2))
				.collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));
	}

	private String parseRequestBody(InputStream inputStream) throws IOException {
		final int bufferLength = 16384;
		final StringBuilder sb = new StringBuilder();
		byte[] bytes = new byte[bufferLength];
		int read;
		while (inputStream.available() > 0) {
			read = inputStream.read(bytes);
			sb.append(new String(bytes, 0, Math.min(read, bufferLength)));
		}
		return sb.toString();
	}

	private void validateResponse(Response resp) {
		if (resp == null) {
			throw new RuntimeException("Response can't be null");
		}
	}

	public static RestServerBuilder builder() {
		return new RestServerBuilder();
	}

	public static class RestServerBuilder {
		private final RestServer server = new RestServer();
		private final List<AbstractEndpoint> endpoints = new LinkedList<>();
		private int port;
		private String url;
		private BasicAuthenticator authenticator;

		public RestServerBuilder setPort(int port) {
			this.port = port;
			return this;
		}

		public RestServerBuilder setUrl(String url) {
			this.url = url;
			return this;
		}

		public RestServerBuilder setAuth(BasicAuthenticator authenticator) {
			this.authenticator = authenticator;
			return this;
		}

		public RestServerBuilder addEndpoint(AbstractEndpoint endpoint) {
			endpoints.add(endpoint);
			return this;
		}

		public RestServer build() throws Exception {
			if (url == null || url.isEmpty()) {
				throw new RuntimeException("Url not set");
			}

			if (port == 0) {
				throw new RuntimeException("Port not set");
			}

			if (endpoints.isEmpty()) {
				throw new RuntimeException("No endpoint processors set");
			}

			this.server.url = url;

			this.server.endpoints = this.endpoints.stream()
					.sorted((x, y) -> -Integer.compare(x.getUrl().length(), y.getUrl().length()))  // from longest to shortest urls
					.toArray(AbstractEndpoint[]::new);

			this.server.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
			HttpContext context = this.server.httpServer.createContext(url, this.server::processRequest);

			if (authenticator != null) {
				context.setAuthenticator(authenticator);
			}

			System.out.println("Starting endpoint: http://localhost:" + port + url);
			this.server.httpServer.setExecutor(null); // creates a default executor
			this.server.httpServer.start();

			return this.server;
		}
	}
}
