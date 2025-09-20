# People of Delivery — Phase 2 (MSA Edition)

## 프로젝트 소개
1차 모놀리식을 **마이크로서비스 아키텍처(MSA)** 로 분해하고, **API Gateway + Service Discovery** 기반으로 서비스 간 독립 배포/확장을 가능하게 만든 단계입니다.  
또한 **CI/CD(Blue/Green)**, **모니터링**, **데이터 파이프라인 & AI**, **토큰 성능 고도화(Refresh→Redis)**, **Mongo 동기화/조회 분리**, **동시성(낙관적 락) 대응** 등의 운영·확장 요소를 도입해 실서비스 수준의 안정성과 생산성을 확보했습니다.

## 개발 기간
- 25.08.07 ~ 25.08.24

## 개발 구성원 및 역할

### 인프라팀
#### [조성규](https://github.com/sungchilll), [김지윤](https://github.com/JIYOOnii007), [정병민](https://github.com/ByeongminJeong)
- **클라우드 & 네트워킹**: VPC, Subnet, ALB/WAF, Route53, 보안그룹
- **서비스 디스커버리 & 게이트웨이**: Eureka 운영, Spring Cloud Gateway 라우팅/필터 정책
- **CI/CD**: GitHub Actions
- **레지스트리 & 아티팩트**: 컨테이너 레지스트리, 이미지 서명/스캔
- **시크릿/구성**: 환경변수·KMS/Secrets Manager 관리(토큰/키/PG 키)
- **모니터링/로깅**: 헬스체크, 로그 수집, 대시보드/알람(3차에서 Observability 확장)
- **성능/비용**: 오토스케일링, 캐시/스토리지 선택, 비용 감시

> **산출물**: 배포 파이프라인, 환경별 매니페스트/변수, 운영 문서(장애대응 Runbook)

### 백엔드 개발팀 
#### [김준형](https://github.com/jh010303), [모시은](https://github.com/shiien14)
- **MSA 서비스 분리**: auth, user, store, cart, payment, ai 각 서비스의 API/비즈니스 로직
- **보안 체계**: Spring Security, JWT(Access/Refresh) 토큰 발급
- **도메인·데이터**: 서비스별 DB/스키마, 엔티티/리포지토리, 마이그레이션 전략
- **대외 연동**: 토스(Toss) 결제 승인/실패/리다이렉트 플로우
- **캐시/세션**: Redis 기반 토큰/조회 캐싱 정책
- **계약/문서화**: 공통 응답/에러 규약, Swagger(OpenAPI) 유지
- **테스트/품질**: 단위/통합 테스트

> **산출물**: 서비스 코드/테스트, API 스펙


## 기술 스택
- **언어/런타임**: Java 21, Gradle (멀티프로젝트)
- **프레임워크**: Spring Boot, Spring MVC, Spring Data JPA, Spring Security
- **MSA 인프라**: Spring Cloud **Gateway**(API 게이트웨이), **Eureka**(서비스 디스커버리)
- **인증/인가**: **JWT**(Access/Refresh)
- **데이터**: PostgreSQL(주 데이터), **MongoDB(조회/분석용)**, **Redis**
- **메시징(초기화)**: Kafka(이벤트 발행 기초—3차에서 본격화)
- **문서/테스트**: Swagger/OpenAPI, JUnit 5
- **배포/운영**: Docker/Compose, GitHub Actions, **AWS OIDC 인증**, **ECS Blue/Green** 배포
- **네트워킹/보안(예)**: Route53, WAF, ALB, VPC-Link
- **모니터링**: (대시보드/로그/헬스체크 체계—3차에서 Observability 확장)

## 핵심 목표
1. **모놀리식 → MSA 분해**: auth / user / store / cart / payment / ai 등 서비스 단위로 책임 경계를 분리  
2. **안전한 진입·라우팅**: API Gateway 단일 진입점 + Service Discovery 기반 **동적 라우팅**  
3. **지속적 전달**: GitHub Actions + **AWS OIDC** + **ECS Blue/Green** 로 **무중단 배포** 파이프라인 구축  
4. **운영 가시성 확보**: 모니터링/로그 체계, 헬스체크 및 장애 탐지의 기반 마련  
5. **데이터·성능 고도화 준비**: **Refresh→Redis** 토큰 관리, **PostgreSQL→Mongo** 실시간 동기화로 조회 분리, 이후 3차의 이벤트/일관성 확장 기반 확보

## 주요 기능
- **Gateway & Discovery**
  - 외부 트래픽의 단일 진입점, 경로/호스트 기반 라우팅, Eureka 기반 **동적 서비스 연결**
- **인증/인가**
  - Spring Security + **JWT**(Access/Refresh), OAuth2(소셜 로그인) → 내부 토큰 발급
  - **Refresh Token in Redis**: 토큰 검증/회수 성능 개선
- **주문/결제/상점/회원/장바구니 서비스 분리**
  - 각 서비스 독립 배포/스케일, 공통 응답/에러 규약 준수
  - **payment-service**: **토스(Toss) API** 연동 승인/실패/리다이렉트 플로우
- **데이터 파이프라인 & AI (ai-service)**
  - **OpenWeather** + **AI 랭킹**(선호/날씨/온도 가중치) 기반 메뉴 추천
  - **Gemini API**로 메뉴/가게 설명 생성
- **데이터 분리/동기화**
  - **PostgreSQL → MongoDB** 실시간 동기화 파이프라인(조회 최적화)
- **동시성/일관성 준비**
  - **낙관적 락**으로 재고 갱신 충돌 완화
- **CI/CD**
  - 변경 모듈만 빌드(효율) / 공통 모듈 변경 시 전체 재빌드(안정)
  - **AWS OIDC** + **ECS Blue/Green** 배포 자동화

## 아키텍처 하이라이트
- **서비스 경계**
  - `apigateway` — 외부 진입점  
  - `discovery` — Eureka Service Registry  
  - `auth-service` / `user-service` / `store-service` / `cart-service` / `payment-service` / `ai-service`  
  - `module-common` — 공통 DTO/에러/유틸 표준화
- **트래픽 흐름**
  - Client → **API Gateway** → (Eureka) → 각 서비스  
  - 내부 통신은 서비스별 API 계약 중심(데이터 조인 대신 API 콜)
- **데이터 계층**
  - 서비스별 DB/스키마 분리 권장(결합도 축소)  
  - **Redis**: 토큰·세션·캐시, **MongoDB**: 읽기/검색 성능 최적화
- **배포/운영**
  - GitHub Actions → **AWS OIDC** 신뢰 기반 권한 위임 → **ECS Blue/Green** 롤아웃  
  - Route53/WAF/ALB/VPC-Link 등으로 외부/내부 트래픽 경로 및 보안 강화
- **관측성**
  - 헬스체크, 로그/지표 수집 기반(3차에서 **Kubernetes + Observability** 본격 적용)

### 파일 구조
```
MSA_people_of_delivery/
├─ apigateway/           # Spring Cloud Gateway (외부 진입점)
├─ discovery/            # Eureka Service Registry
├─ auth-service/         # 인증/인가
├─ user-service/         # 회원
├─ store-service/        # 상점/메뉴
├─ cart-service/         # 장바구니
├─ payment-service/      # 결제(Toss 연동)
├─ ai-service/           # 추천/설명 등 AI 확장 포인트
├─ module-common/        # 공통 DTO/에러/유틸/보안 컴포넌트
├─ .github/              # GitHub Actions 등 워크플로우
├─ build.gradle          # 루트 빌드 스크립트
├─ settings.gradle       # 멀티프로젝트 설정(includes)
├─ gradle/ , gradlew(*)  # Gradle wrapper
└─ README.md

```

## 인프라 구성도
<img width="1454" height="702" alt="스크린샷 2025-09-21 오전 1 16 22" src="https://github.com/user-attachments/assets/e9c0eafd-05f3-41c8-92a4-485d33810705" />


## Commit Message Convention

| Tag Name       | Description                                    |
|----------------|------------------------------------------------|
| :sparkles: Feat    | 새로운 기능을 추가                              |
| :bug: Fix          | 버그 수정                                      |
| :art: Style        | 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우 |
| :hammer:  Refactor | 프로덕션 코드 리팩토링                         |
| :memo: Docs        | 문서 수정                                      |
| :test_tube: Test   | 테스트 코드, 리팩토링 테스트 코드 추가, Production Code(실제로 사용하는 코드) 변경 없음 |
| :rocket: Chore     | 빌드 업무 수정, 패키지 매니저 수정, 패키지 관리자 구성 등 업데이트, Production Code 변경 없음 |
