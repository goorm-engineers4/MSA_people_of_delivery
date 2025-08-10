package com.example.cloudfour.aiservice.service.command;

import com.example.cloudfour.aiservice.client.GeminiClient;
import com.example.cloudfour.aiservice.dto.GeminiResponseDTO;
import com.example.cloudfour.aiservice.dto.RestaurantDescriptionRequestDTO;
import com.example.cloudfour.aiservice.dto.RestaurantDescriptionResponseDTO;
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
public class RestaurantDescriptionCommandServiceImpl {
    
    private final GeminiClient geminiClient;
    private final AiLogRepository aiLogRepository;
    public Mono<RestaurantDescriptionResponseDTO> generateRestaurantDescription(RestaurantDescriptionRequestDTO request) {
        String prompt = buildPrompt(request);
        String requestType = "RESTAURANT_DESCRIPTION";
        return geminiClient.generateContent(prompt)
                .map(this::parseGeminiResponse)
                .map(response -> {
                    log.info("[AI_LOG] question: {} | result: {}", prompt, response.getGeneratedDescription());
                    saveAiLog(prompt, response.getGeneratedDescription(), true, null, requestType);
                    return response;
                })
                .onErrorResume(error -> {
                    log.error("가게 설명 생성 실패: {}", error.getMessage());
                    RestaurantDescriptionResponseDTO errorResponse = RestaurantDescriptionResponseDTO.builder()
                            .success(false)
                            .errorMessage("가게 설명 생성에 실패했습니다: " + error.getMessage())
                            .build();
                    log.info("[AI_LOG] question: {} | result: {}", prompt, error.getMessage());
                    saveAiLog(prompt, error.getMessage(), false, error.getMessage(), requestType);
                    return Mono.just(errorResponse);
                });
    }
    
    private String buildPrompt(RestaurantDescriptionRequestDTO request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("아래 가게 정보를 참고하여, 손님이 방문하고 싶어질 만한 매력적인 가게 소개글을 한두 문장으로 자연스럽게 작성해 주세요. ");
        prompt.append("가게의 특징, 장점, 인기 메뉴, 분위기, 추천 포인트 등을 간단하게 소개해 주세요. ");
        prompt.append("정보가 부족하면 상상력을 발휘해 예시로 채워도 좋습니다.\n\n");
        
        if (request.getStore() != null) {
            prompt.append("가게명: ").append(request.getStore().getName()).append("\n");
            prompt.append("카테고리: ").append(request.getStore().getStoreCategory()).append("\n");
            if (request.getStore().getAddress() != null && !request.getStore().getAddress().isEmpty()) {
                prompt.append("주소: ").append(request.getStore().getAddress()).append("\n");
            }
            if (request.getStore().getPhone() != null && !request.getStore().getPhone().isEmpty()) {
                prompt.append("전화번호: ").append(request.getStore().getPhone()).append("\n");
            }
            if (request.getStore().getOperationHours() != null && !request.getStore().getOperationHours().isEmpty()) {
                prompt.append("영업시간: ").append(request.getStore().getOperationHours()).append("\n");
            }
            if (request.getStore().getClosedDays() != null && !request.getStore().getClosedDays().isEmpty()) {
                prompt.append("휴무일: ").append(request.getStore().getClosedDays()).append("\n");
            }
            if (request.getStore().getContent() != null && !request.getStore().getContent().isEmpty()) {
                prompt.append("가게 소개: ").append(request.getStore().getContent()).append("\n");
            }
            if (request.getStore().getMinPrice() != null) {
                prompt.append("최소주문금액: ").append(request.getStore().getMinPrice()).append("원\n");
            }
            if (request.getStore().getDeliveryTip() != null) {
                prompt.append("배달팁: ").append(request.getStore().getDeliveryTip()).append("원\n");
            }
            if (request.getStore().getRating() != null) {
                prompt.append("평점: ").append(request.getStore().getRating()).append("\n");
            }
            if (request.getStore().getLikeCount() != null) {
                prompt.append("좋아요 수: ").append(request.getStore().getLikeCount()).append("\n");
            }
            if (request.getStore().getReviewCount() != null) {
                prompt.append("리뷰 수: ").append(request.getStore().getReviewCount()).append("\n");
            }
            if (request.getStore().getStorePicture() != null && !request.getStore().getStorePicture().isEmpty()) {
                prompt.append("가게 사진: ").append(request.getStore().getStorePicture()).append("\n");
            }
        }
        
        prompt.append("\n아래 형식에 맞춰 반드시 모든 항목을 채워서 응답해 주세요.\n");
        prompt.append("가게 설명: (예: 홍대입구 명물, '행복한 분식'! 신선한 재료로 만든 다양한 분식 메뉴로 여러분의 입맛을 사로잡습니다.)\n");
        prompt.append("환영 메시지: (예: 안녕하세요! 행복한 분식에 오신 것을 환영합니다. 정성껏 준비한 맛있는 분식으로 여러분의 하루에 행복을 더해드릴게요!)\n");
        prompt.append("분위기 설명: (예: 깔끔하고 활기 넘치는 분위기 속에서 맛있는 분식을 즐기실 수 있습니다.)\n");
        prompt.append("추천 메뉴: 3~5개, 쉼표로 구분 (예: 떡볶이, 튀김, 김밥)\n");
        prompt.append("추천 태그: 3~5개, 쉼표로 구분 (예: 분식, 가성비, 혼밥, 포장, 배달)\n");
        prompt.append("정보가 부족하면 창의적으로 예시를 채워도 좋습니다.\n");
        
        return prompt.toString();
    }
    
    private RestaurantDescriptionResponseDTO parseGeminiResponse(GeminiResponseDTO geminiResponse) {
        if (geminiResponse.getCandidates() == null || geminiResponse.getCandidates().isEmpty()) {
            return RestaurantDescriptionResponseDTO.builder()
                    .success(false)
                    .errorMessage("AI 응답이 비어있습니다.")
                    .build();
        }
        String responseText = geminiResponse.getCandidates().get(0).getContent().getParts().get(0).getText();
        String[] sections = responseText.split("\n\n");
        String description = "";
        String welcomeMessage = "";
        String atmosphereDescription = "";
        String recommendedDishes = "";
        String suggestedTags = "";
        for (String section : sections) {
            if (section.contains("가게 설명")) {
                description = section.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (section.contains("환영 메시지")) {
                welcomeMessage = section.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (section.contains("분위기 설명")) {
                atmosphereDescription = section.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (section.contains("추천 메뉴")) {
                recommendedDishes = section.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (section.contains("추천 태그")) {
                suggestedTags = section.replaceAll("^.*?[:.]\\s*", "").trim();
            }
        }
        return RestaurantDescriptionResponseDTO.builder()
                .generatedDescription(description)
                .welcomeMessage(welcomeMessage)
                .atmosphereDescription(atmosphereDescription)
                .recommendedDishes(recommendedDishes)
                .suggestedTags(suggestedTags)
                .success(true)
                .build();
    }
    
    private void saveAiLog(String question, String result, Boolean success, String errorMessage, String requestType) {
        try {
            AiLog aiLog = AiLog.builder()
                    .question(question)
                    .result(result)
                    .success(success)
                    .errorMessage(errorMessage)
                    .requestType(requestType)
                    .build();
            
            aiLogRepository.save(aiLog);
            log.info("AI 로그 저장 완료: {}", aiLog.getId());
        } catch (Exception e) {
            log.error("AI 로그 저장 실패: {}", e.getMessage());
            throw new AiLogException(AiLogErrorCode.CREATE_FAILED);
        }
    }
}
