package com.fmi.relovut.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeTracker {
	private long startTime;
	
	@Before("execution (* com.fmi.relovut.controllers..*(..))")
	public void BeforeControllerMethodCalled() {
		startTime = System.nanoTime();
	}
	
	@After("execution (* com.fmi.relovut.controllers..*(..))")
	public void AfterControllerMethodCalled(JoinPoint joinPoint) {
		long endTime = System.nanoTime();
		System.out.println("Execution time from " + joinPoint.getSignature().toShortString() + ": " + (endTime - startTime) / 1000000 + " msec" );
	}
}
