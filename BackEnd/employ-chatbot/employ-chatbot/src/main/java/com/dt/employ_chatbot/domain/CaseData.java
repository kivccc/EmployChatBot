package com.dt.employ_chatbot.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "labor_case")
public class CaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String caseType; //자료 구분

    @Column(length = 1000)
    private String caseSource; //제목

    private String organ; //위원회 정보

    private LocalDate caseTime; //작성일

}
