/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.repository.BatchCycleRepository;
import com.aia.print.agent.repository.BatchFileDetailsRepository;

/**
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 24 Nov 2019 7:06:47 am
 */
@RestController
public class BatchCycleController {

    /**
     * batchCycleRepository
     */
    @Autowired
    private BatchCycleRepository batchCycleRepository;

    /**
     * batchFileDetailsRepository
     */
    @Autowired
    private BatchFileDetailsRepository batchFileDetailsRepository;

    /**
     * @return
     */
    @GetMapping("/batchcycle/list")
    public List< BatchCycle > findAll(@RequestParam(value = "cycleDate", required = false) String cycleDate) {
        if (StringUtils.isBlank(cycleDate)) {
            return batchCycleRepository.getAllBatchCycleList();
        } else {
            return batchCycleRepository.getAllBatchCycleByDate(cycleDate);
        }
    }

    @GetMapping("/batchcycle/filelist")
    public List< BatchFileDetails > findBatchFileDetails(@RequestParam(value = "batchId", required = false) Long batchId) {
        if (batchId != null) {
            return batchFileDetailsRepository.getBatchFileDetails(batchId);
        } else {
            return new ArrayList< BatchFileDetails >();
        }
    }

}
