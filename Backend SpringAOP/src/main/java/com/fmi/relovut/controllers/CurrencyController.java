package com.fmi.relovut.controllers;

import com.fmi.relovut.annotations.CacheableRequest;
import com.fmi.relovut.dto.currency.CurrencyDto;
import com.fmi.relovut.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {
    @Autowired
    private CurrencyService currencyService;

    @GetMapping("")
    @CacheableRequest(SecondsUntilExpiry = 10)
    public List<CurrencyDto> getCurrencies() throws Exception {
    	//throw new Exception("Error because I want to!");
        return currencyService.getCurrencies();
    }
}
