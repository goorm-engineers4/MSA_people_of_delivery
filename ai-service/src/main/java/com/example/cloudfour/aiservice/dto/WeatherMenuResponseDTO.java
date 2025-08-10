package com.example.cloudfour.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherMenuResponseDTO {
   private String recommendedMenu;
   private String restaurantName;
   private String restaurantAddress;
   private List<String> alternativeMenus;
   private List<String> alternativeRestaurants;
   private String reasoning;
   private String weatherAdvice;
   private Boolean success;
   private String errorMessage;
   private String weatherDescKo;
}
