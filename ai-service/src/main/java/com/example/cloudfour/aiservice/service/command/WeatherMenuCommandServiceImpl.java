package com.example.cloudfour.aiservice.service.command;

import com.example.cloudfour.aiservice.client.GeminiClient;
import com.example.cloudfour.aiservice.client.WeatherClient;
import com.example.cloudfour.aiservice.client.UserServiceClient;
import com.example.cloudfour.aiservice.dto.GeminiResponseDTO;
import com.example.cloudfour.aiservice.dto.WeatherMenuRequestDTO;
import com.example.cloudfour.aiservice.dto.WeatherMenuResponseDTO;
import com.example.cloudfour.aiservice.entity.AiLog;
import com.example.cloudfour.aiservice.exception.AiLogErrorCode;
import com.example.cloudfour.aiservice.exception.AiLogException;
import com.example.cloudfour.aiservice.repository.AiLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherMenuCommandServiceImpl {
    
    private final GeminiClient geminiClient;
    private final WeatherClient weatherClient;
    private final AiLogRepository aiLogRepository;
    private final UserServiceClient userServiceClient;

    private static final Map<Integer, String> WEATHER_DESC_KO_MAP = new HashMap<>();
    static {
        WEATHER_DESC_KO_MAP.put(201, "가벼운 비를 동반한 천둥구름");
        WEATHER_DESC_KO_MAP.put(200, "비를 동반한 천둥구름");
        WEATHER_DESC_KO_MAP.put(202, "폭우를 동반한 천둥구름");
        WEATHER_DESC_KO_MAP.put(210, "약한 천둥구름");
        WEATHER_DESC_KO_MAP.put(211, "천둥구름");
        WEATHER_DESC_KO_MAP.put(212, "강한 천둥구름");
        WEATHER_DESC_KO_MAP.put(221, "불규칙적 천둥구름");
        WEATHER_DESC_KO_MAP.put(230, "약한 연무를 동반한 천둥구름");
        WEATHER_DESC_KO_MAP.put(231, "연무를 동반한 천둥구름");
        WEATHER_DESC_KO_MAP.put(232, "강한 안개비를 동반한 천둥구름");
        WEATHER_DESC_KO_MAP.put(300, "가벼운 안개비");
        WEATHER_DESC_KO_MAP.put(301, "안개비");
        WEATHER_DESC_KO_MAP.put(302, "강한 안개비");
        WEATHER_DESC_KO_MAP.put(310, "가벼운 적은비");
        WEATHER_DESC_KO_MAP.put(311, "적은비");
        WEATHER_DESC_KO_MAP.put(312, "강한 적은비");
        WEATHER_DESC_KO_MAP.put(313, "소나기와 안개비");
        WEATHER_DESC_KO_MAP.put(314, "강한 소나기와 안개비");
        WEATHER_DESC_KO_MAP.put(321, "소나기");
        WEATHER_DESC_KO_MAP.put(500, "악한 비");
        WEATHER_DESC_KO_MAP.put(501, "중간 비");
        WEATHER_DESC_KO_MAP.put(502, "강한 비");
        WEATHER_DESC_KO_MAP.put(503, "매우 강한 비");
        WEATHER_DESC_KO_MAP.put(504, "극심한 비");
        WEATHER_DESC_KO_MAP.put(511, "우박");
        WEATHER_DESC_KO_MAP.put(520, "약한 소나기 비");
        WEATHER_DESC_KO_MAP.put(521, "소나기 비");
        WEATHER_DESC_KO_MAP.put(522, "강한 소나기 비");
        WEATHER_DESC_KO_MAP.put(531, "불규칙적 소나기 비");
        WEATHER_DESC_KO_MAP.put(600, "가벼운 눈");
        WEATHER_DESC_KO_MAP.put(601, "눈");
        WEATHER_DESC_KO_MAP.put(602, "강한 눈");
        WEATHER_DESC_KO_MAP.put(611, "진눈깨비");
        WEATHER_DESC_KO_MAP.put(612, "소나기 진눈깨비");
        WEATHER_DESC_KO_MAP.put(615, "약한 비와 눈");
        WEATHER_DESC_KO_MAP.put(616, "비와 눈");
        WEATHER_DESC_KO_MAP.put(620, "약한 소나기 눈");
        WEATHER_DESC_KO_MAP.put(621, "소나기 눈");
        WEATHER_DESC_KO_MAP.put(622, "강한 소나기 눈");
        WEATHER_DESC_KO_MAP.put(701, "박무");
        WEATHER_DESC_KO_MAP.put(711, "연기");
        WEATHER_DESC_KO_MAP.put(721, "연무");
        WEATHER_DESC_KO_MAP.put(731, "모래 먼지");
        WEATHER_DESC_KO_MAP.put(741, "안개");
        WEATHER_DESC_KO_MAP.put(751, "모래");
        WEATHER_DESC_KO_MAP.put(761, "먼지");
        WEATHER_DESC_KO_MAP.put(762, "화산재");
        WEATHER_DESC_KO_MAP.put(771, "돌풍");
        WEATHER_DESC_KO_MAP.put(781, "토네이도");
        WEATHER_DESC_KO_MAP.put(800, "구름 한 점 없는 맑은 하늘");
        WEATHER_DESC_KO_MAP.put(801, "약간의 구름이 낀 하늘");
        WEATHER_DESC_KO_MAP.put(802, "드문드문 구름이 낀 하늘");
        WEATHER_DESC_KO_MAP.put(803, "구름이 거의 없는 하늘");
        WEATHER_DESC_KO_MAP.put(804, "구름으로 뒤덮인 흐린 하늘");
        WEATHER_DESC_KO_MAP.put(900, "토네이도");
        WEATHER_DESC_KO_MAP.put(901, "태풍");
        WEATHER_DESC_KO_MAP.put(902, "허리케인");
        WEATHER_DESC_KO_MAP.put(903, "한랭");
        WEATHER_DESC_KO_MAP.put(904, "고온");
        WEATHER_DESC_KO_MAP.put(905, "바람부는");
        WEATHER_DESC_KO_MAP.put(906, "우박");
        WEATHER_DESC_KO_MAP.put(951, "바람이 거의 없는");
        WEATHER_DESC_KO_MAP.put(952, "약한 바람");
        WEATHER_DESC_KO_MAP.put(953, "부드러운 바람");
        WEATHER_DESC_KO_MAP.put(954, "중간 세기 바람");
        WEATHER_DESC_KO_MAP.put(955, "신선한 바람");
        WEATHER_DESC_KO_MAP.put(956, "센 바람");
        WEATHER_DESC_KO_MAP.put(957, "돌풍에 가까운 센 바람");
        WEATHER_DESC_KO_MAP.put(958, "돌풍");
        WEATHER_DESC_KO_MAP.put(959, "심각한 돌풍");
        WEATHER_DESC_KO_MAP.put(960, "폭풍");
        WEATHER_DESC_KO_MAP.put(961, "강한 폭풍");
        WEATHER_DESC_KO_MAP.put(962, "허리케인");
    }

    private String getWeatherDescKo(Integer code) {
        return code != null && WEATHER_DESC_KO_MAP.containsKey(code) ? WEATHER_DESC_KO_MAP.get(code) : "알 수 없음";
    }

    public Mono<WeatherMenuResponseDTO> recommendMenuByWeather(WeatherMenuRequestDTO request) {
        String city = request.getCity();
        String weather = request.getWeather();
        

        if (request.getUserId() != null) {
            return userServiceClient.getUserAddresses(request.getUserId().toString())
                    .flatMap(addresses -> {
                        if (!addresses.isEmpty()) {
                            String userCity = addresses.get(0).getAddress();
                            log.info("사용자 주소 기반 도시 설정: {}", userCity);
                            return processWeatherMenuRecommendation(userCity, weather);
                        } else {
                            String defaultCity = city != null ? city : "서울";
                            return processWeatherMenuRecommendation(defaultCity, weather);
                        }
                    })
                    .onErrorResume(error -> {
                        log.warn("사용자 주소 정보 조회 실패, 기본 도시 사용: {}", error.getMessage());
                        String defaultCity = city != null ? city : "서울";
                        return processWeatherMenuRecommendation(defaultCity, weather);
                    });
        } else {
            String defaultCity = city != null ? city : "서울";
            return processWeatherMenuRecommendation(defaultCity, weather);
        }
    }
    
    private Mono<WeatherMenuResponseDTO> processWeatherMenuRecommendation(String city, String weather) {
        return weatherClient.getCurrentWeather(city)
                .flatMap(weatherData -> {
                    String weatherDescKo = getWeatherDescKo(weatherData.getCode());
                    return generateWeatherMenuRecommendation(
                        city,
                        weather,
                        weatherDescKo,
                        weatherData.getTemperature(),
                        weatherData.getFeelsLike(),
                        weatherData.getHumidity(),
                        weatherData.getWindSpeed(),
                        weatherData.getRain1h()
                    ).map(response -> {
                        response.setWeatherDescKo(weatherDescKo);
                        return response;
                    });
                });
    }

    private Mono<WeatherMenuResponseDTO> generateWeatherMenuRecommendation(String city, String weather, String weatherDescKo, String temp, Double feelsLike, String humidity, Double windSpeed, Double rain1h) {
        String prompt = buildWeatherMenuPrompt(city, weather, weatherDescKo, temp, feelsLike, humidity, windSpeed, rain1h);
        String requestType = "WEATHER_MENU";
        return geminiClient.generateContent(prompt)
                .map(this::parseWeatherMenuResponse)
                .doOnNext(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
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
                    log.error("날씨 기반 메뉴 추천 실패: {}", error.getMessage());
                    WeatherMenuResponseDTO errorResponse = WeatherMenuResponseDTO.builder()
                            .success(false)
                            .errorMessage("날씨 기반 메뉴 추천에 실패했습니다: " + error.getMessage())
                            .build();
                    String resultJson;
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
                        resultJson = objectMapper.writeValueAsString(errorResponse);
                    } catch (Exception e) {
                        resultJson = "AI 응답 JSON 변환 실패: " + e.getMessage();
                    }
                    saveAiLog(prompt, resultJson, false, error.getMessage(), requestType);
                    return Mono.just(errorResponse);
                });
    }

    private String buildWeatherMenuPrompt(String city, String weather, String weatherDescKo, String temp, Double feelsLike, String humidity, Double windSpeed, Double rain1h) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("아래 정보를 참고하여 반드시 모든 항목을 빠짐없이 채워서, 손님이 읽기 좋게 자연스럽고 구체적으로 작성해 주세요.\n");
        prompt.append("정보가 부족하면 창의적으로 예시로 채워도 좋습니다.\n\n");
        prompt.append("도시: ").append(city).append("\n");
        if (weather != null && !weather.isBlank()) {
            prompt.append("날씨: ").append(weather).append("\n");
        }
        if (weatherDescKo != null && !weatherDescKo.isBlank() && !weatherDescKo.equals("알 수 없음")) {
            prompt.append("한글 날씨 설명: ").append(weatherDescKo).append("\n");
            prompt.append("특히 '날씨 조언'에는 한글 날씨 설명(예: ").append(weatherDescKo).append(")을 참고하여, 오늘의 날씨에 맞는 조언을 자연스럽게 작성해 주세요.\n");
        }

        if (temp != null || feelsLike != null || humidity != null || windSpeed != null || rain1h != null) {
            prompt.append("상세 날씨 정보: ");
            if (temp != null) prompt.append("현재 온도: ").append(temp).append("°C, ");
            if (feelsLike != null) prompt.append("체감 온도: ").append(feelsLike).append("°C, ");
            if (humidity != null) prompt.append("습도: ").append(humidity).append("%, ");
            if (windSpeed != null) prompt.append("풍속: ").append(windSpeed).append("m/s, ");
            if (rain1h != null) prompt.append("강수량(1시간): ").append(rain1h).append("mm, ");
            prompt.append("\n");
        }
        prompt.append("\n아래 형식에 맞춰 반드시 모든 항목을 채워서 응답해 주세요.\n");
        prompt.append("추천 메뉴: (예: 시원한 콩국수)\n");
        prompt.append("추천 가게: (예: 진주회관 (시청))\n");
        prompt.append("가게 주소: (예: 서울특별시 중구 세종대로 11길 20)\n");
        prompt.append("추천 이유: (예: 새콤달콤한 양념에 비벼 먹는 비빔국수는 입맛을 돋우는 데 제격입니다.)\n");
        prompt.append("대안 메뉴: (예: 비빔국수)\n");
        prompt.append("대안 가게: (예: 할머니국수 (종로))\n");
        prompt.append("날씨 조언: (예: 오늘처럼 맑은 날에는 시원한 콩국수로 더위를 식혀보세요!)\n");
        prompt.append("오늘 날씨 요약: (예: 서울 오늘은 맑음, 기온 25°C, 습도 60%, 바람 약함, 체감온도 27°C로 쾌적한 날씨입니다.)\n");
        return prompt.toString();
    }

    private WeatherMenuResponseDTO parseWeatherMenuResponse(GeminiResponseDTO geminiResponse) {
        if (geminiResponse.getCandidates() == null || geminiResponse.getCandidates().isEmpty()) {
            return WeatherMenuResponseDTO.builder()
                    .success(false)
                    .errorMessage("AI 응답이 비어있습니다.")
                    .build();
        }
        
        String responseText = geminiResponse.getCandidates().get(0).getContent().getParts().get(0).getText();
        

        String[] lines = responseText.split("\n");
        
        String recommendedMenu = "";
        String restaurantName = "";
        String restaurantAddress = "";
        String reasoning = "";
        String alternativeMenus = "";
        String alternativeRestaurants = "";
        String weatherAdvice = "";
        String todayWeatherSummary = "";
        
        for (String line : lines) {
            if (line.contains("추천 메뉴:")) {
                recommendedMenu = line.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (line.contains("추천 가게:")) {
                restaurantName = line.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (line.contains("가게 주소:")) {
                restaurantAddress = line.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (line.contains("추천 이유:")) {
                reasoning = line.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (line.contains("대안 메뉴:")) {
                alternativeMenus = line.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (line.contains("대안 가게:")) {
                alternativeRestaurants = line.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (line.contains("날씨 조언:")) {
                weatherAdvice = line.replaceAll("^.*?[:.]\\s*", "").trim();
            } else if (line.contains("오늘 날씨 요약:")) {
                todayWeatherSummary = line.replaceAll("^.*?[:.]\\s*", "").trim();
            }
        }
        
        List<String> alternativeMenuList = Arrays.asList(alternativeMenus.split(","));
        List<String> alternativeRestaurantList = Arrays.asList(alternativeRestaurants.split(","));
        
        log.info("AI 응답 파싱 결과 - recommendedMenu: '{}', restaurantName: '{}'", 
                recommendedMenu, restaurantName);
        
        return WeatherMenuResponseDTO.builder()
                .recommendedMenu(recommendedMenu)
                .restaurantName(restaurantName)
                .restaurantAddress(restaurantAddress)
                .reasoning(reasoning)
                .alternativeMenus(alternativeMenuList)
                .alternativeRestaurants(alternativeRestaurantList)
                .weatherAdvice(weatherAdvice)
                .todayWeatherSummary(todayWeatherSummary)
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
