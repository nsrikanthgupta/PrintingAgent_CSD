package com.aia.print.agent.jobs;

import java.text.ParseException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Configuring the All Scheduler Jobs
 * 
 * @author Srikanth Neerumalla
 */
@Configuration
public class SchedulerConfigurator {

    private static final Logger LOGGER = LogManager.getLogger(SchedulerConfigurator.class);

    /**
     * instanceName
     */
    @Value("${org.quartz.scheduler.instanceName}")
    private String instanceName;

    /**
     * instanceId
     */
    @Value("${org.quartz.scheduler.instanceId}")
    private String instanceId;

    /**
     * threadCount
     */
    @Value("${org.quartz.threadPool.threadCount}")
    private String threadCount;

    /**
     * verifyReconcilePattern
     */
    @Value("${print.agent.verify.reconcile.pattren}")
    private String verifyReconcilePattern;

    /**
     * generateTemplatePattern
     */
    @Value("${print.agent.generate.template.pattren}")
    private String generateTemplatePattern;

    /**
     * fileDownloadPattern
     */
    @Value("${print.agent.file.download.pattren}")
    private String fileDownloadPattern;

    /**
     * checkCyclePattern
     */
    @Value("${print.agent.check.cycle.pattren}")
    private String checkCyclePattern;

    /**
     * claimstatementRestClientPattern
     * 
     */

    @Value("${print.agent.claimstatement.pattren}")
    private String claimStatementPattern;

    /**
     * Build and returns the JobFactory to configure the Jobs
     * 
     * @param applicationContext : {@link ApplicationContext}
     * @return <b>JobFactory</b> : {@link JobFactory}
     */
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * Configuring Quartz Scheduler and Register a list of Trigger objects with the Scheduler
     * 
     * @param applicationContext : {@link ApplicationContext}
     * @return <b>SchedulerFactoryBean</b> : {@link SchedulerFactoryBean}
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setJobFactory(jobFactory(applicationContext));
        Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.scheduler.instanceName", instanceName);
        quartzProperties.setProperty("org.quartz.scheduler.instanceId", instanceId);
        quartzProperties.setProperty("org.quartz.threadPool.threadCount", threadCount);
        factory.setQuartzProperties(quartzProperties);
        int index = 0;
        Trigger[] triggers = new Trigger[4];
        // Trigger[] triggers = new Trigger[1];
        triggers[index++] = CheckCyleDateTrigger().getObject();
        triggers[index++] = FileDownloadJobTrigger().getObject();
        triggers[index++] = GenerateTemplateTrigger().getObject();
        triggers[index++] = VerifyReconcileDataTrigger().getObject();
        // triggers[index++] = ClaimStatementJobTrigget().getObject();
        factory.setTriggers(triggers);
        return factory;
    }

    private CronTriggerFactoryBean VerifyReconcileDataTrigger() {
        try {
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            JobDetailFactoryBean detailFactoryBean = getJobDetail("com.aia.print.agent.jobs.VerifyReconcileData");
            if (detailFactoryBean != null) {
                cronTriggerFactoryBean.setJobDetail(detailFactoryBean.getObject());
            }
            cronTriggerFactoryBean.setBeanName("VerifyReconcileTrigger");
            cronTriggerFactoryBean.setCronExpression(verifyReconcilePattern);
            cronTriggerFactoryBean.afterPropertiesSet();
            return cronTriggerFactoryBean;
        } catch (RuntimeException | ParseException e) {
            LOGGER.error("Excepiton Occured While Creating Cron Factory: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param factoryBean
     * @return
     */
    private JobDetailFactoryBean getJobDetail(String factoryBean) {
        try {
            Class< ? > cls = Class.forName(factoryBean);
            Job obj = (Job) cls.newInstance();
            JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
            jobDetailFactoryBean.setJobClass(obj.getClass());
            jobDetailFactoryBean.setDescription(factoryBean);
            jobDetailFactoryBean.setDurability(true);
            jobDetailFactoryBean.setBeanName(factoryBean.concat("JobDetail"));
            jobDetailFactoryBean.setName(factoryBean);
            jobDetailFactoryBean.afterPropertiesSet();
            return jobDetailFactoryBean;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error("Excepiton Occured While Constructing Job Detail Factory Bean : " + e.getMessage(), e);
        }
        return null;
    }

    private CronTriggerFactoryBean GenerateTemplateTrigger() {
        try {
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            JobDetailFactoryBean detailFactoryBean = getJobDetail("com.aia.print.agent.jobs.GenerateTemplate");
            if (detailFactoryBean != null) {
                cronTriggerFactoryBean.setJobDetail(detailFactoryBean.getObject());
            }
            cronTriggerFactoryBean.setBeanName("GenerateTemplateTrigger");
            cronTriggerFactoryBean.setCronExpression(generateTemplatePattern);
            cronTriggerFactoryBean.afterPropertiesSet();
            return cronTriggerFactoryBean;
        } catch (RuntimeException | ParseException e) {
            LOGGER.error("Excepiton Occured While Creating Cron Factory: " + e.getMessage(), e);
        }
        return null;
    }

    private CronTriggerFactoryBean FileDownloadJobTrigger() {
        try {
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            JobDetailFactoryBean detailFactoryBean = getJobDetail("com.aia.print.agent.jobs.FileDownloadJob");
            if (detailFactoryBean != null) {
                cronTriggerFactoryBean.setJobDetail(detailFactoryBean.getObject());
            }
            cronTriggerFactoryBean.setBeanName("FileDownloadJobTrigger");
            cronTriggerFactoryBean.setCronExpression(fileDownloadPattern);
            cronTriggerFactoryBean.afterPropertiesSet();
            return cronTriggerFactoryBean;
        } catch (RuntimeException | ParseException e) {
            LOGGER.error("Excepiton Occured While Creating Cron Factory: " + e.getMessage(), e);
        }
        return null;
    }

    private CronTriggerFactoryBean CheckCyleDateTrigger() {
        try {
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            JobDetailFactoryBean detailFactoryBean = getJobDetail("com.aia.print.agent.jobs.CheckCyleDateJob");
            if (detailFactoryBean != null) {
                cronTriggerFactoryBean.setJobDetail(detailFactoryBean.getObject());
            }
            cronTriggerFactoryBean.setBeanName("CheckCyleDateTrigger");
            cronTriggerFactoryBean.setCronExpression(checkCyclePattern);
            cronTriggerFactoryBean.afterPropertiesSet();
            return cronTriggerFactoryBean;
        } catch (RuntimeException | ParseException e) {
            LOGGER.error("Excepiton Occured While Creating Cron Factory: " + e.getMessage(), e);
        }
        return null;
    }

    private CronTriggerFactoryBean ClaimStatementJobTrigget() {
        try {
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            JobDetailFactoryBean detailFactoryBean = getJobDetail("com.aia.print.agent.jobs.ClaimStatementJob");
            if (detailFactoryBean != null) {
                cronTriggerFactoryBean.setJobDetail(detailFactoryBean.getObject());
            }
            cronTriggerFactoryBean.setBeanName("ClaimStatementJob");
            cronTriggerFactoryBean.setCronExpression(claimStatementPattern);
            cronTriggerFactoryBean.afterPropertiesSet();
            return cronTriggerFactoryBean;
        } catch (RuntimeException | ParseException e) {
            LOGGER.error("Excepiton Occured While Creating Cron Factory: " + e.getMessage(), e);
        }
        return null;
    }
}