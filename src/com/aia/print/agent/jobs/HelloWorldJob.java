/**
 * 
 */
package com.aia.print.agent.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aia.print.agent.service.HelloWorldService;

/**
 * @author srineeru
 *
 */
public class HelloWorldJob implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldJob.class);
	
	/**
     * 
     */
    @Autowired
    private HelloWorldService helloWorldService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.debug("HelloWorldJob Triggered");
		helloWorldService.getHelloMessage();
	}

}
