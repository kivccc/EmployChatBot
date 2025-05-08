package com.dt.employ_chatbot.controller;


import com.dt.employ_chatbot.domain.CaseData;
import com.dt.employ_chatbot.dto.CaseRequest;
import com.dt.employ_chatbot.service.CaseSimilarityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CaseController {

    private final CaseSimilarityService caseSimilarityService;

    @PostMapping("/test")
    public ResponseEntity<String> test(@RequestBody String userInput){
        CaseData mostSimilar= caseSimilarityService.findMostSimilarCase(userInput);
        if (mostSimilar == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("유사한 판례를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(mostSimilar.getCaseSource());
    }
    @PostMapping("/test2")
    public ResponseEntity<List<CaseData>> test2(@RequestBody String userInput){
        List<CaseData> result = caseSimilarityService.findSimilarCases(userInput);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/case")
    public ResponseEntity<List<CaseData>> searchCase(@RequestBody CaseRequest caseRequest){
        List<CaseData> result = caseSimilarityService.findTopSimilarCases(caseRequest.getKeyWord(),2);
        System.out.println(result);
        return ResponseEntity.ok(result);
    }
}
