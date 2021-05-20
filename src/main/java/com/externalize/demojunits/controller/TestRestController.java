package com.externalize.demojunits.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

	private static final Logger LOGGER =  LoggerFactory.getLogger(TestRestController.class);
	
	

	@GetMapping("/sample")
	public String sampleTest() {
	
		LOGGER.info("Got it");
		return "Success";
	}
}
