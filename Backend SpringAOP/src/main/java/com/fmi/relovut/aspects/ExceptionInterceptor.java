package com.fmi.relovut.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fmi.relovut.helpers.Logger;

@Aspect
@Component
public class ExceptionInterceptor {
	@Autowired
	private Logger logger;
	
	@Pointcut("execution(* com.fmi.relovut.controllers..*(..))")
	public void controllerMethod() {}
	
	@AfterThrowing(pointcut = "controllerMethod()", throwing = "ex")
	public void exceptionInterceptor(JoinPoint joinPoint, Exception ex) {
		if (!(ex instanceof ResponseStatusException)) {
			logger.log(joinPoint.getSignature() + " has thrown an exception with the message: " + ex.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}
}
