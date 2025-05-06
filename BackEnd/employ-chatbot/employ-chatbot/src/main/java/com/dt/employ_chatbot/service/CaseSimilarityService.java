package com.dt.employ_chatbot.service;


import com.dt.employ_chatbot.domain.CaseData;
import com.dt.employ_chatbot.repository.LaborCaseRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.phrase_extractor.KoreanPhraseExtractor;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.stereotype.Service;
import scala.collection.Seq;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseSimilarityService {

    private final LaborCaseRepository laborCaseRepository;

    private final Map<Long, Set<String>> caseTokenCache = new HashMap<>();

    @PostConstruct
    public void init(){
        log.info("DB 판례 토큰화 작업 시작");
        List<CaseData> allCaseData=laborCaseRepository.findAll();
        if(allCaseData.isEmpty())
            log.error("DB내 판례 케이스 존재하지않음");
        for(CaseData caseData:allCaseData){
            String text=caseData.getCaseSource();
            Set<String> tokens=tokenizePhrase(text);
            caseTokenCache.put(caseData.getId(), tokens);
        }
    }

    /// 유사 판례 1개 찾기(기본 토큰 기준)
    public CaseData findMostSimilarCase(String userInput) {

        Set<String> inputTokens = tokenize(userInput);
        long bestId = -1;
        double bestScore = -1;

        for (Map.Entry<Long, Set<String>> entry : caseTokenCache.entrySet()) {
            double sim = computeJaccardSimilarity(inputTokens, entry.getValue());
            if (sim > bestScore) {
                bestScore = sim;
                bestId = entry.getKey();
            }
        }

        return bestId != -1 ? laborCaseRepository.findById(bestId).orElse(null) : null;
    }

    /// 유사 판례 3개 찾기(기본 토큰 기준)
    public List<CaseData> findSimilarCases(String userInput) {
        Set<String> inputTokens = tokenize(userInput);
        return laborCaseRepository.findAll().stream()
                .map(caseData -> Map.entry(caseData, computeJaccardSimilarity(inputTokens, tokenize(caseData.getCaseSource()))))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double computeJaccardSimilarity(Set<String> tokens1, Set<String> tokens2) {
        Set<String> intersection = new HashSet<>(tokens1);
        intersection.retainAll(tokens2);

        Set<String> union = new HashSet<>(tokens1);
        union.addAll(tokens2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private Set<String> tokenize(String text){
        CharSequence normalized= OpenKoreanTextProcessorJava.normalize(text);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<String> tokenList = OpenKoreanTextProcessorJava.tokensToJavaStringList(tokens);
        return new HashSet<>(tokenList);
    }




    /// 사용자 입력을 phrase 기반 토큰화
    private Set<String> tokenizePhrase(String text) {
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<KoreanPhraseExtractor.KoreanPhrase> phrases =
                OpenKoreanTextProcessorJava.extractPhrases(tokens, true, false);

        return phrases.stream()
                .map(KoreanPhraseExtractor.KoreanPhrase::text)
                .collect(Collectors.toSet());
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    public List<CaseData> findTopSimilarCases(String userInput, int topN) {
        Set<String> inputTokens = tokenizePhrase(userInput);

        return laborCaseRepository.findAll().stream()
                .map(caseData -> {
                    Set<String> caseTokens = caseTokenCache.get(caseData.getId());
                    double similarity = caseTokens != null ? calculateJaccardSimilarity(inputTokens, caseTokens) : 0.0;
                    return new AbstractMap.SimpleEntry<>(caseData, similarity);
                })
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    ///
}
