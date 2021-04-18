package com.fmi.relovut.aspects;

import java.security.Principal;
import java.util.HashMap;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fmi.relovut.dto.transactions.CreateTransactionDto;
import com.fmi.relovut.helpers.Logger;
import com.fmi.relovut.models.Account;
import com.fmi.relovut.repositories.AccountRepository;

@Aspect
@Component
public class TransactionsCache {
	private static HashMap<String, Object> _cache = new HashMap<String, Object>();
	
	@Autowired
    private AccountRepository accountRepository;
	
	@Autowired
	private Logger logger;
	
	
	@Around("execution (@org.springframework.web.bind.annotation.GetMapping * com.fmi.relovut.controllers.TransactionController.*(..)) && args(principal, ..)")
	public Object getRequest(ProceedingJoinPoint joinPoint, Principal principal) throws Throwable {
		String methodName = joinPoint.getSignature().getName();
		String key = principal.getName() + "-" + methodName;
		
		if (_cache.containsKey(key)) {
			logger.log("TransactionsCache (getRequest): Retrieving response from cache for user with email: " + principal.getName());
			return _cache.get(key);
		}
		
		Object result = joinPoint.proceed(joinPoint.getArgs());
		logger.log("TransactionsCache (getRequest): Adding cache entry for user with email: " + principal.getName());
		_cache.put(key, result);
		return result;
	}
	
	@Around("execution (!@org.springframework.web.bind.annotation.GetMapping * com.fmi.relovut.controllers.TransactionController.*(..)) && args(principal, ..)")
	public Object genericNonGetRequest(ProceedingJoinPoint joinPoint, Principal principal) throws Throwable {
		Object result = joinPoint.proceed(joinPoint.getArgs());
		logger.log("TransactionsCache (genericNonGetRequest): Invalidating cache for user with email: " + principal.getName());
		_cache.keySet().removeIf(k -> k.startsWith(principal.getName() + "-"));
		return result;
	}
	
	@Around("execution (!@org.springframework.web.bind.annotation.GetMapping * com.fmi.relovut.controllers.TransactionController.*(..)) && args(principal, dto)")
	public Object createTransactionRequest(ProceedingJoinPoint joinPoint, Principal principal, CreateTransactionDto dto) throws Throwable {
		// Execute
		Object result = joinPoint.proceed(joinPoint.getArgs());
		
		// Afterwards, get destination account for the transaction
		Optional<Account> destinationAccount = accountRepository.findById(dto.getToAccountId());
		if (destinationAccount.isPresent()) {
			// Get that user's email
			String email = destinationAccount.get().getUser().getEmail();
			logger.log("TransactionsCache (createTransactionRequest): Invalidating cache for destination user with email: " + email);
			_cache.keySet().removeIf(k -> k.startsWith(email + "-"));
		}
		
		return result;
	}
}
