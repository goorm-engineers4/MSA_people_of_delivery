package com.example.cloudfour.aiservice.converter;

import com.example.cloudfour.aiservice.dto.ProductDescriptionResponseDTO;

public class ProductDescriptionConverter {
    
    public static ProductDescriptionResponseDTO toProductDescriptionResponseDTO(boolean success, String errorMessage,
            String generatedDescription, String marketingCopy, String keyFeatures, String suggestedTags) {
        return ProductDescriptionResponseDTO.builder()
                .success(success)
                .errorMessage(errorMessage)
                .generatedDescription(generatedDescription)
                .marketingCopy(marketingCopy)
                .keyFeatures(keyFeatures)
                .suggestedTags(suggestedTags)
                .build();
    }
}
