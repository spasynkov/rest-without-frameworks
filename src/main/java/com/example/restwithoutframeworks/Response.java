package com.example.restwithoutframeworks;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
	private String text;
	private int responseCode;
}
