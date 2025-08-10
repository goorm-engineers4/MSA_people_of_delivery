package com.example.cloudfour.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDescriptionResponseDTO {
    private boolean success;
    private String errorMessage;
    private String generatedDescription;
    private String marketingCopy;
    private String keyFeatures;
    private String suggestedTags;
}
