package com.example.cloudfour.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDescriptionRequestDTO {
    private String name;
    private String productName;
    private String category;
    private String ingredients;
    private String price;
}
