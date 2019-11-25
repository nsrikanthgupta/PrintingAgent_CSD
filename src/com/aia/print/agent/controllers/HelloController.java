/* Project Mercury
 * Copyright (c) 2017-2018 Deutsche Post DHL
 * All rights reserved.
 */

package com.aia.print.agent.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aia.print.agent.entiry.TableDmDoc;
import com.aia.print.agent.repository.TableDmDocRepository;
import com.aia.print.agent.service.HelloWorldService;

/**
 * 
 * @author srineeru
 */
@RestController
public class HelloController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);

    /**
     * 
     */
    @Autowired
    private HelloWorldService helloWorldService;

    @Autowired
    private TableDmDocRepository tableDmDocRepository;

    @GetMapping("/hello")
    @ResponseBody
    public String findAll() {
        List< TableDmDoc > list = tableDmDocRepository.getTableDmDoc("50003022", "P967143", "PrintingAgent_CSD", "2019-11-21");
        for(TableDmDoc doc : list) {
            LOGGER.info("{} - {} - {} - {} - {}", doc.getProposalNo(), doc.getBillNo(), doc.getDocCreationDt(), doc.getCreatedBy(), doc.getMtDocTypecd());
        }
        return helloWorldService.getHelloMessage();
    }

}
