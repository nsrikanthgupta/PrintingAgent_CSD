/**
 * 
 */
package com.aia.print.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.repository.CompanyCodeRepository;

/**
 * @author srineeru
 *
 */
@Service
public class HelloWorldServiceImpl implements HelloWorldService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldServiceImpl.class);
    
    @Autowired
    private CompanyCodeRepository companyCodeRepository;

	@Override
	public String getHelloMessage() {
	    String tba = "TBA";
	    CompanyCode companyCode = new CompanyCode();
	    companyCode.setCompanyCode("CMP_".concat(String.valueOf(System.currentTimeMillis())));
	    companyCode.setFolderPath(tba);
	    companyCode.setIpAddress(tba);
	    companyCode.setLatestCycleDate("");
	    companyCode.setPassword(tba);
	    companyCode.setUsername(tba);
	    companyCode.setLocalFolderPath(tba);
	    companyCode.setStatus(tba);
	    //companyCodeRepository.save(companyCode);
	    LOGGER.debug(companyCode.toString());
		return "Hello";
	}

}
