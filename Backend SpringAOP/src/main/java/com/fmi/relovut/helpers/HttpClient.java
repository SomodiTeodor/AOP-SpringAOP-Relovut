package com.fmi.relovut.helpers;

import org.springframework.web.client.RestTemplate;

public class HttpClient<T> {
	private Class<T> type;
	
	public HttpClient(Class<T> type) {
		this.type = type;
	}
	
	public T get(String url) {
		try {
			return new RestTemplate().getForObject(url, type);			
		}
		catch (Exception ex) {
			System.out.println("c");
			return null;
		}
	}
}
