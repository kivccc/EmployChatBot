package com.dt.employ_chatbot.controller;

import com.dt.employ_chatbot.dto.KeyWordRequest;
import com.dt.employ_chatbot.service.RuleSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RuleSearchController {

    private final RuleSearchService ruleSearchService;

    @PostMapping("/rule")
    public ResponseEntity<Mono<String>> search(@RequestBody KeyWordRequest request){
        log.info("Rule 검색 키워드 : "+request.getKeyword());
        Mono<String> result=ruleSearchService.callRuleApi(request.getKeyword(), request.getPageNo());
        return ResponseEntity.ok(result);
    }
}
