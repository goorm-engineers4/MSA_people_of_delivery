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
                .map(response -> {
                    saveAiLog(prompt, response.getGeneratedDescription(), true, null, requestType);
                    return response;
                })
                .onErrorResume(error -> {
                    log.error("상품 설명 생성 실패: {}", error.getMessage());
                    ProductDescriptionResponseDTO errorResponse = ProductDescriptionResponseDTO.builder()
                            .success(false)
                            .errorMessage("상품 설명 생성에 실패했습니다: " + error.getMessage())
                            .build();
                    saveAiLog(prompt, error.getMessage(), false, error.getMessage(), requestType);
                    return Mono.just(errorResponse);
                });
    }
    
    private String buildPrompt(ProductDescriptionRequestDTO request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("아래 메뉴명을 참고하여, 사장님이 메뉴 등록 시 입력하는 메뉴 설명을 한두 문장으로 자연스럽고 구체적으로 작성해 주세요. ");
        prompt.append("메뉴의 특징, 장점, 맛, 추천 포인트 등을 간단하게 소개해 주세요. ");
        prompt.append("정보가 부족하면 창의적으로 예시로 채워도 좋습니다.\n\n");
        prompt.append("메뉴명: ").append(request.getName()).append("\n\n");
        prompt.append("아래 형식에 맞춰 반드시 한두 문장으로 메뉴 설명을 작성해 주세요.\n");
        prompt.append("메뉴 설명: (예: 고소한 콩물과 쫄깃한 면발이 어우러진 여름철 별미 콩국수입니다. 시원하게 즐기세요!)\n");
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


        return ProductDescriptionResponseDTO.builder()
                .generatedDescription(responseText != null ? responseText.trim() : "")
                .marketingCopy("")
                .keyFeatures("")
                .suggestedTags("")
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
