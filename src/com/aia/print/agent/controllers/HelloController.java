/* Project Mercury
 * Copyright (c) 2017-2018 Deutsche Post DHL
 * All rights reserved.
 */

package com.aia.print.agent.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.aia.print.agent.service.HelloWorldService;

/**
 * 
 * @author srineeru
 */
@RestController
public class HelloController {

	/**
	 * 
	 */
	@Autowired
	private HelloWorldService helloWorldService;

	@GetMapping("/hello")
	@ResponseBody
	public String findAll() {
		return helloWorldService.getHelloMessage();
	}
	
	/*@PostMapping(path="/download",produces = "application/json", consumes="application/json")
	public String getClaimStatementStatus(@RequestBody ArrayList<ModelClass> downloadSelected) {
		
		return '';
	}*/
	
	@GetMapping(path="/getdetails",produces = "application/json", consumes="application/json")
	public String getClaimStatement() {
		
		
		return "Hello";
	}
}
