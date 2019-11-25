/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.controllers;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aia.print.agent.service.BatchReconciliationService;

/**
 * @author Srikanth Neerumalla
 * @DateTime 25 Nov 2019 2:24:27 pm
 */
@RestController
public class PolicyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    private BatchReconciliationService batchReconciliationService;

    @GetMapping("/policy/info")
    public Set< String > findAll(@RequestParam(value = "cycleDate", required = false) String cycleDate,
        @RequestParam(value = "debtorCode", required = false) String debtorCode) {
        LOGGER.debug("Policy Info Request Received for the Cycle Date {} and DebtorCode {} ", cycleDate, debtorCode);
        return batchReconciliationService.getPolicyInfo(cycleDate, debtorCode);

    }

}
