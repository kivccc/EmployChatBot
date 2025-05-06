package com.dt.employ_chatbot.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class CaseDataCsv {
    private String id; // 구분
    private String number; // 순번
    private String caseType; // 자료구분
    private String caseSource; // 제목
    private String organ; // 위원회코드
    private String caseTime; // 작성일
    private String viewCount; // 조회수
}
