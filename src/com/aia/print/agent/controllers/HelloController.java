/* Project Mercury
 * Copyright (c) 2017-2018 Deutsche Post DHL
 * All rights reserved.
 */

package com.aia.print.agent.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.service.BatchReconciliationService;
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

    /**
     * batchReconciliationService
     */
    @Autowired
    private BatchReconciliationService batchReconciliationService;

    @GetMapping("/hello")
    @ResponseBody
    public String findAll() {
        return helloWorldService.getHelloMessage();
    }

    @GetMapping(path = "/getdetails", produces = "application/json", consumes = "application/json")
    public String getClaimStatement() {
        BatchFileDetails batchFileDetails = new BatchFileDetails();
        batchFileDetails
            .setFileLocation("C://Users/Lenovo/Documents/GitHub/PrintAgent/Co3_Reconcilation_Report_20191114_0233.csv");
        batchFileDetails.setBatchId(1000l);
        batchReconciliationService.processReconcilationFile(batchFileDetails);
        return "Hello";
    }
}
