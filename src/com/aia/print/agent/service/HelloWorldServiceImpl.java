/**
 * 
 */
package com.aia.print.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author srineeru
 *
 */
@Service
public class HelloWorldServiceImpl implements HelloWorldService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldServiceImpl.class);

    @Override
    public String getHelloMessage() {
        LOGGER.debug("Hello");
        return "Hello";
    }

}
