package com.db.trades.tradesbatch.jobs;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.db.trades.tradesbatch.tasklet.ProcessMaturedTrades;

@Configuration
@EnableBatchProcessing
public class ProcessMaturedTradesJob {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private SimpleJobLauncher jobLauncher;
	
	@Scheduled(cron = "*/5 * * * * *")
	public void perform() throws Exception {

		System.out.println("Job Started at :" + new Date());

		JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();

		JobExecution execution = jobLauncher.run(processMaturedTrades(), param);

		System.out.println("Job finished with status :" + execution.getStatus());
	}
	
	@Bean
	public Job processMaturedTrades() {	
		return jobBuilderFactory.get("processMaturedTrades").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").tasklet(new ProcessMaturedTrades()).build();
	}
	
}
