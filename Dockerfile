# syntax=docker/dockerfile:1

############################
# 1) Build stage
############################
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Gradle wrapper/설정 선복사 (캐시 극대화)
COPY gradlew gradle/ settings.gradle* build.gradle* ./
# 모듈의 build.gradle도 선복사 (없으면 무시)
COPY module-common/build.gradle* module-common/
COPY discovery/build.gradle* discovery/
COPY apigateway/build.gradle* apigateway/
COPY user-service/build.gradle* user-service/
COPY store-service/build.gradle* store-service/
COPY cart-service/build.gradle* cart-service/
COPY ai-service/build.gradle* ai-service/

RUN chmod +x gradlew
# 의존성 캐시 웜업 (소스 없이도 가능한 최소 작업)
RUN ./gradlew --no-daemon -q help || true

# 실제 소스 복사
COPY . .

# 빌드할 모듈 지정 (예: discovery, apigateway, user-service ...)
ARG SERVICE
RUN ./gradlew --no-daemon :${SERVICE}:bootJar -x test

############################
# 2) Runtime stage
############################
FROM eclipse-temurin:21-jre
WORKDIR /app

# 메모리/타임존 기본값
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Seoul"

# 빌드 산출물 복사
ARG SERVICE
# ▼ 모든 서비스 모듈의 bootJar 파일명을 app.jar로 고정하면 가장 깔끔합니다.
COPY --from=build /workspace/${SERVICE}/build/libs/app.jar /app/app.jar

ENTRYPOINT ["sh","-c","java $JAVA_TOOL_OPTIONS -jar /app/app.jar"]
