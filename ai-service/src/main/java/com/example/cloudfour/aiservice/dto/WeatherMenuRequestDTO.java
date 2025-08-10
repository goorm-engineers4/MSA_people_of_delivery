package com.example.cloudfour.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherMenuRequestDTO {
    private Long userId;
    private String city;
    private String weather;
}
