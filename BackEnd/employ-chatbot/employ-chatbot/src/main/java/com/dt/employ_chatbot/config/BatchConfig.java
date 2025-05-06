package com.dt.employ_chatbot.config;

import com.dt.employ_chatbot.domain.CaseData;
import com.dt.employ_chatbot.dto.CaseDataCsv;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Configuration
@RequiredArgsConstructor
//@EnableBatchProcessing 없애야 table 자동생성
@Slf4j
public class BatchConfig {
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean
    public FlatFileItemReader<CaseDataCsv> caseDataReader(){
        return new FlatFileItemReaderBuilder<CaseDataCsv>()
                .name("caseDataReader")
                .resource(new ClassPathResource("cases.csv"))
                .delimited()
                .names("id","number", "caseType", "caseSource", "organ", "caseTime", "viewCount")
                .linesToSkip(1)
                .encoding("EUC-KR")
                .targetType(CaseDataCsv.class)
                .build();
    }

    @Bean
    public ItemProcessor<CaseDataCsv, CaseData> processor() {
        return item -> {
            CaseData data = new CaseData();

            // null 체크
            if (item == null) {
                log.warn("입력 항목이 null입니다");
                return null; // null 반환하면 해당 항목은 건너뜁니다
            }

            data.setCaseType(item.getCaseType());
            data.setCaseSource(item.getCaseSource());
            data.setOrgan(item.getOrgan());

            // 날짜 변환 예외 처리
            if (item.getCaseTime() != null && !item.getCaseTime().isEmpty()) {
                try {
                    data.setCaseTime(LocalDate.parse(item.getCaseTime()));
                } catch (DateTimeParseException e) {
                    log.error("날짜 변환 오류: {} - {}", item.getCaseTime(), e.getMessage());
                    // 기본값 설정 또는 null 유지
                    // data.setCaseTime(LocalDate.now()); // 기본값으로 현재 날짜 설정
                }
            }

            return data;
        };
    }


    @Bean
    public JpaItemWriter<CaseData> caseDataWriter(){
        JpaItemWriter<CaseData> writer=new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }



    @Bean
    public Step importLaborCaseStep(){
        return new StepBuilder("importLaborCaseStep",jobRepository)
                .<CaseDataCsv,CaseData>chunk(10,transactionManager)
                .reader(caseDataReader())
                .processor(processor())
                .writer(caseDataWriter())
                .build();
    }
    @Bean
    public Job importCaseJob(JobRepository jobRepository, Step step1){
        return new JobBuilder("myjob",jobRepository)
                .start(importLaborCaseStep())
                .build();
    }

}
