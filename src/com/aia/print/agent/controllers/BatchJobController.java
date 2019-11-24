/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.controllers;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aia.print.agent.entiry.BatchJobConfig;
import com.aia.print.agent.repository.BatchJobConfigRepository;

/**
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 24 Nov 2019 6:42:50 am
 */
@RestController
public class BatchJobController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobController.class);

    /**
     * batchJobConfigRepository
     */
    @Autowired
    private BatchJobConfigRepository batchJobConfigRepository;

    /**
     * @return
     */
    @GetMapping("/batch/list/active")
    public List< BatchJobConfig > findAllActive() {
        return batchJobConfigRepository.getBatchJobConfig("ACTIVE");
    }

    /**
     * @return
     */
    @GetMapping("/batch/list/inactive")
    public List< BatchJobConfig > findAllInActive() {
        return batchJobConfigRepository.getBatchJobConfig("INACTIVE");
    }

    /**
     * @return
     */
    @GetMapping("/batch/list/make/inactive")
    public List< BatchJobConfig > makeInactive() {
        List< BatchJobConfig > list = batchJobConfigRepository.getBatchJobConfig("ACTIVE");
        if (CollectionUtils.isNotEmpty(list)) {
            for (BatchJobConfig batchJobConfig : list) {
                batchJobConfig.setStatus("INACTIVE");
                batchJobConfig.setUpdatedBy("SD");
                batchJobConfig.setUpdatedDate(new Date());
                batchJobConfigRepository.save(batchJobConfig);
                LOGGER.info("{} becomes INACTIVE ", batchJobConfig.getJobKey());
            }
        }
        return batchJobConfigRepository.getBatchJobConfig("INACTIVE");
    }

    /**
     * @return
     */
    @GetMapping("/batch/list/make/active")
    public List< BatchJobConfig > makeActive() {
        List< BatchJobConfig > list = batchJobConfigRepository.getBatchJobConfig("INACTIVE");
        if (CollectionUtils.isNotEmpty(list)) {
            for (BatchJobConfig batchJobConfig : list) {
                batchJobConfig.setStatus("ACTIVE");
                batchJobConfig.setUpdatedBy("SD");
                batchJobConfig.setUpdatedDate(new Date());
                batchJobConfigRepository.save(batchJobConfig);
                LOGGER.info("{} becomes ACTIVE ", batchJobConfig.getJobKey());
            }
        }
        return batchJobConfigRepository.getBatchJobConfig("ACTIVE");
    }
}
