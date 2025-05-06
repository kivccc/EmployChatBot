package com.dt.employ_chatbot;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class EmployChatbotApplication {

	private final JobLauncher jobLauncher;
	private final Job importCaseJob;


	public static void main(String[] args) {
		SpringApplication.run(EmployChatbotApplication.class, args);
		System.out.println("안녕");
	}

	/*
	@Bean
	public CommandLineRunner run(){
		return args -> {
			JobParameters jobParameter=new JobParametersBuilder()
					.addLong("time",System.currentTimeMillis())
					.toJobParameters();
			jobLauncher.run(importCaseJob,jobParameter);
		};
	}

	 */

}
