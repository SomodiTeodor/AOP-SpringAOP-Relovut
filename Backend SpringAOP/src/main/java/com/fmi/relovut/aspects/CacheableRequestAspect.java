package com.fmi.relovut.aspects;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fmi.relovut.annotations.CacheableRequest;
import com.fmi.relovut.helpers.Logger;

@Aspect
@Component
public class CacheableRequestAspect {
	@Autowired
	private Logger logger;
	
	private static HashMap<String, Response> _cache = new HashMap<String, Response>();
	
	@Pointcut("execution(@com.fmi.relovut.annotations.CacheableRequest * com.fmi.relovut.controllers..*(..));")
	public void cacheableMethod() {}
	
	@Around("cacheableMethod()")
	public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		Response response;
		Calendar now = Calendar.getInstance();
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		
		// Check cache for existing valid response
		String key = signature.toString();
		if (_cache.containsKey(key)) {
			response = _cache.get(key);
			if (now.before(response.validUntil)) {
				long timeToExpiration = response.validUntil.getTimeInMillis() - now.getTimeInMillis();
				logger.log("CacheableRequestAspect (" + key + ") - found key in cache, expires in " + timeToExpiration + "ms. Returning response from cache.");
				return response.response;
			} else {
				logger.log("CacheableRequestAspect (" + key + ") - found key in cache, but key was expired. Removing and executing...");
				_cache.remove(key);
			}
		}
		
		// Get @CacheableRequest annotation value
		Method method = signature.getMethod();
		CacheableRequest annotation = method.getAnnotation(CacheableRequest.class);
		
		//Annotation[] annotation = thisJoinPoint.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotations();
		if (annotation == null) {
			return joinPoint.proceed();
		}
		
		int secondsUntilExpiry = annotation.SecondsUntilExpiry();
		now.add(Calendar.SECOND, secondsUntilExpiry);
		Object result = joinPoint.proceed();
		
		// Let request execute, then save the result in cache
		response = new Response(now, result);
		logger.log("CacheableRequestAspect (" + key + ") - added key in cache.");
		_cache.put(key, response);
		return result;
	}
	
	private class Response {
		public Response(Calendar validUntil, Object response) {
			this.validUntil = validUntil;
			this.response = response;
		}
		
		public Calendar validUntil;
		public Object response;
	}
}
