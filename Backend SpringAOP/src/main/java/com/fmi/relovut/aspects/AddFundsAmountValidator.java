package com.fmi.relovut.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fmi.relovut.helpers.Logger;

@Aspect
@Component
public class AddFundsAmountValidator {
	@Autowired
	private Logger logger;
	
	@Before("execution(* com.fmi.relovut.services.AddFundsService.addFunds(..)) && args(.., newValue)")
	public void beforeMethod(Double newValue) {
		if (newValue <= 0.0d) {
			logger.log("AddFunds validation error: Amount of " + newValue + " is <= 0. Throwing error...");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"From aspect: Amount cannot be less than or equal to 0!");
		}
	}
}
