package com.example.cloudfour.aiservice.service.command;

import com.example.cloudfour.aiservice.client.GeminiClient;
import com.example.cloudfour.aiservice.dto.GeminiResponseDTO;
import com.example.cloudfour.aiservice.dto.ProductDescriptionRequestDTO;
import com.example.cloudfour.aiservice.dto.ProductDescriptionResponseDTO;
import com.example.cloudfour.aiservice.entity.AiLog;
import com.example.cloudfour.aiservice.exception.AiLogErrorCode;
import com.example.cloudfour.aiservice.exception.AiLogException;
import com.example.cloudfour.aiservice.repository.AiLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDescriptionCommandServiceImpl {
    
    private final GeminiClient geminiClient;
    private final AiLogRepository aiLogRepository;
    public Mono<ProductDescriptionResponseDTO> generateProductDescription(ProductDescriptionRequestDTO request) {
        String prompt = buildPrompt(request);
        String requestType = "PRODUCT_DESCRIPTION";
        return geminiClient.generateContent(prompt)
                .map(this::parseGeminiResponse)
                .doOnNext(response -> {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
                        String resultJson = objectMapper.writeValueAsString(response);
                        log.info("AI 로그 저장 시도 - prompt 길이: {}, result 길이: {}", 
                                prompt != null ? prompt.length() : 0, 
                                resultJson != null ? resultJson.length() : 0);
                        saveAiLog(prompt, resultJson, true, null, requestType);
                    } catch (Exception e) {
                        log.error("AI 응답 JSON 변환 실패: {}", e.getMessage());
                        saveAiLog(prompt, "AI 응답 JSON 변환 실패: " + e.getMessage(), false, e.getMessage(), requestType);
                    }
                })
                .onErrorResume(error -> {
                    log.error("상품 설명 생성 실패: {}", error.getMessage());
                    ProductDescriptionResponseDTO errorResponse = ProductDescriptionResponseDTO.builder()
                            .success(false)
                            .errorMessage("상품 설명 생성에 실패했습니다: " + error.getMessage())
                            .build();
                    String resultJson;
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
                        resultJson = objectMapper.writeValueAsString(errorResponse);
                    } catch (Exception e) {
                        resultJson = "AI 응답 JSON 변환 실패: " + e.getMessage();
                    }
                    saveAiLog(prompt, resultJson, false, error.getMessage(), requestType);
                    return Mono.just(errorResponse);
                });
    }
    
    private String buildPrompt(ProductDescriptionRequestDTO request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("아래 메뉴 정보를 참고하여, 사장님이 메뉴 등록 시 사용할 수 있는 다양한 설명을 작성해 주세요.\n\n");
        prompt.append("메뉴명: ").append(request.getName()).append("\n");
        if (request.getCategory() != null) prompt.append("카테고리: ").append(request.getCategory()).append("\n");
        if (request.getIngredients() != null) prompt.append("재료: ").append(request.getIngredients()).append("\n");
        if (request.getPrice() != null) prompt.append("가격: ").append(request.getPrice()).append("\n");
        prompt.append("\n");
        prompt.append("\n아래 형식에 맞춰 반드시 모든 항목을 채워서 응답해 주세요.\n");
        prompt.append("메뉴 설명: (예: 육즙 가득한 불고기를 특제 소스로 맛을 내어 풍성하게 담았습니다.)\n");
        prompt.append("마케팅 문구: (예: 달콤 짭짤한 불고기버거의 풍미를 느껴보세요!)\n");
        prompt.append("핵심 특징: (예: 신선한 쇠고기와 특제 소스의 완벽한 조화)\n");
        prompt.append("추천 태그: 3~5개, 쉼표로 구분 (예: 불고기,한식,육류,특제소스,든든한)\n");
        prompt.append("정보가 부족하면 창의적으로 예시를 채워도 좋습니다.");
        return prompt.toString();
    }

    private ProductDescriptionResponseDTO parseGeminiResponse(GeminiResponseDTO geminiResponse) {
        if (geminiResponse.getCandidates() == null || geminiResponse.getCandidates().isEmpty()) {
            return ProductDescriptionResponseDTO.builder()
                    .success(false)
                    .errorMessage("AI 응답이 비어있습니다.")
                    .build();
        }

        String responseText = geminiResponse.getCandidates().get(0).getContent().getParts().get(0).getText();
        
        if (responseText == null || responseText.trim().isEmpty()) {
            return ProductDescriptionResponseDTO.builder()
                    .success(false)
                    .errorMessage("AI 응답 텍스트가 비어있습니다.")
                    .build();
        }

        log.info("AI 원본 응답: {}", responseText);
        
        String description = "";
        String marketingCopy = "";
        String keyFeatures = "";
        String suggestedTags = "";
        
        // 여러 줄로 분리하여 각 줄을 확인
        String[] lines = responseText.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            log.info("파싱 중인 줄: '{}'", line);
            
            if (line.contains("메뉴 설명") || line.contains("메뉴설명")) {
                description = line.replaceAll("^.*?[:.]\\s*", "").trim();
                log.info("메뉴 설명 파싱: '{}'", description);
            } else if (line.contains("마케팅 문구") || line.contains("마케팅문구")) {
                marketingCopy = line.replaceAll("^.*?[:.]\\s*", "").trim();
                log.info("마케팅 문구 파싱: '{}'", marketingCopy);
            } else if (line.contains("핵심 특징") || line.contains("핵심특징")) {
                keyFeatures = line.replaceAll("^.*?[:.]\\s*", "").trim();
                log.info("핵심 특징 파싱: '{}'", keyFeatures);
            } else if (line.contains("추천 태그") || line.contains("추천태그")) {
                suggestedTags = line.replaceAll("^.*?[:.]\\s*", "").trim();
                log.info("추천 태그 파싱: '{}'", suggestedTags);
            }
        }
        
        // 만약 파싱이 실패했다면 전체 텍스트에서 키워드로 찾기
        if (description.isEmpty() && responseText.contains("메뉴 설명")) {
            int start = responseText.indexOf("메뉴 설명");
            int end = responseText.indexOf("마케팅 문구");
            if (end == -1) end = responseText.indexOf("**마케팅 문구");
            if (end == -1) end = responseText.length();
            
            if (start != -1 && end != -1 && start < end) {
                description = responseText.substring(start + 5, end).trim();
                log.info("키워드 검색으로 메뉴 설명 파싱: '{}'", description);
            }
        }
        
        if (marketingCopy.isEmpty() && responseText.contains("마케팅 문구")) {
            int start = responseText.indexOf("마케팅 문구");
            int end = responseText.indexOf("핵심 특징");
            if (end == -1) end = responseText.indexOf("**핵심 특징");
            if (end == -1) end = responseText.length();
            
            if (start != -1 && end != -1 && start < end) {
                marketingCopy = responseText.substring(start + 6, end).trim();
                log.info("키워드 검색으로 마케팅 문구 파싱: '{}'", marketingCopy);
            }
        }
        
        if (keyFeatures.isEmpty() && responseText.contains("핵심 특징")) {
            int start = responseText.indexOf("핵심 특징");
            int end = responseText.indexOf("추천 태그");
            if (end == -1) end = responseText.indexOf("**추천 태그");
            if (end == -1) end = responseText.length();
            
            if (start != -1 && end != -1 && start < end) {
                keyFeatures = responseText.substring(start + 5, end).trim();
                log.info("키워드 검색으로 핵심 특징 파싱: '{}'", keyFeatures);
            }
        }
        
        if (suggestedTags.isEmpty() && responseText.contains("추천 태그")) {
            int start = responseText.indexOf("추천 태그");
            if (start != -1) {
                suggestedTags = responseText.substring(start + 5).trim();
                log.info("키워드 검색으로 추천 태그 파싱: '{}'", suggestedTags);
            }
        }
        
        return ProductDescriptionResponseDTO.builder()
                .generatedDescription(description)
                .marketingCopy(marketingCopy)
                .keyFeatures(keyFeatures)
                .suggestedTags(suggestedTags)
                .success(true)
                .build();
    }
    
    private void saveAiLog(String question, String result, Boolean success, String errorMessage, String requestType) {
        try {
            log.info("AI 로그 저장 시작 - question 길이: {}, result 길이: {}, success: {}, requestType: {}", 
                    question != null ? question.length() : 0,
                    result != null ? result.length() : 0,
                    success,
                    requestType);
            
            if (question != null && question.length() > 100) {
                log.info("Question (첫 100자): {}", question.substring(0, 100) + "...");
            }
            if (result != null && result.length() > 100) {
                log.info("Result (첫 100자): {}", result.substring(0, 100) + "...");
            }
            
            AiLog aiLog = AiLog.builder()
                    .question(question)
                    .result(result)
                    .success(success)
                    .errorMessage(errorMessage)
                    .requestType(requestType)
                    .build();
            
            aiLogRepository.save(aiLog);
            log.info("AI 로그 저장 완료: ID={}, question 길이={}, result 길이={}", 
                    aiLog.getId(), 
                    aiLog.getQuestion() != null ? aiLog.getQuestion().length() : 0,
                    aiLog.getResult() != null ? aiLog.getResult().length() : 0);
        } catch (Exception e) {
            log.error("AI 로그 저장 실패: {}", e.getMessage());
            throw new AiLogException(AiLogErrorCode.CREATE_FAILED);
        }
    }
}
