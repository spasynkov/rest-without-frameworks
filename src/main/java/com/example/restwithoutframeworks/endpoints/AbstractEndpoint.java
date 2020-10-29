package com.example.restwithoutframeworks.endpoints;

import com.example.restwithoutframeworks.Request;
import com.example.restwithoutframeworks.Response;
import com.example.restwithoutframeworks.exceptions.RequestMethodNotImplementedException;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class AbstractEndpoint {
	protected String url;

	public Response processGet(Request request) {
		return throwMethodNotOverwritten();
	}

	public Response processPost(Request request) {
		return throwMethodNotOverwritten();
	}

	public Response processPut(Request request) {
		return throwMethodNotOverwritten();
	}

	public Response processDelete(Request request) {
		return throwMethodNotOverwritten();
	}

	private Response throwMethodNotOverwritten() {
		throw new RequestMethodNotImplementedException("Method was not overwritten");
	}
}
