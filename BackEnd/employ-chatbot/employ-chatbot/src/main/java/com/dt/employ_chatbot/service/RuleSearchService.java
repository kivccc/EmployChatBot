package com.dt.employ_chatbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class RuleSearchService {


    @Value("${rule.api.key}")
    private String apiKey;

    public Mono<String> callRuleApi(String keyWord) {
        WebClient webClient = WebClient.create();  // WebClient 인스턴스 생성
        log.info("API Key: {}", apiKey);

        final var builder = new DefaultUriBuilderFactory();
        builder.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        final String uriString = builder.builder()
                .scheme("https")
                .host("apis.data.go.kr")
                .path("/B552468/srch/smartSearch")
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1)
                .queryParam("searchValue", URLEncoder.encode(keyWord, StandardCharsets.UTF_8))  // 여기만 인코딩
                .queryParam("category", 0)
                .build()
                .toString();

        log.info("Final URI: {}", uriString);

        URI uri = URI.create(uriString);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }

}
