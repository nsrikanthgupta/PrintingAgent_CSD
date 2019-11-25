/* Project Mercury
 * Copyright (c) 2017-2018 Deutsche Post DHL
 * All rights reserved.
 */

package com.aia.print.agent.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

}
