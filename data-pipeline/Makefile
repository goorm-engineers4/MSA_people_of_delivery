# 🍜 AI 메뉴 랭킹 시스템 - 데이터 파이프라인 자동화
# 사용법: make ingest

.PHONY: help ingest quality-check performance-test clean

# 기본 설정
PYTHON = python3
DATA_DIR = ./data
LOG_DIR = ./logs
TIMESTAMP = $(shell date +%Y%m%d_%H%M%S)

help: ## 사용 가능한 명령어 표시
	@echo "🍜 AI 메뉴 랭킹 시스템 - 데이터 파이프라인"
	@echo "================================================"
	@echo "사용 가능한 명령어:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'
	@echo ""

ingest: ## PostgreSQL 기반 데이터 파이프라인 실행 (운영DB → Analytics Schema)
	@echo "🚀 PostgreSQL 데이터 파이프라인 시작: $(TIMESTAMP)"
	@mkdir -p $(LOG_DIR)
	
	@echo "1️⃣ 메뉴 데이터 동기화 중 (store-service → analytics)..."
	@psql -h localhost -U postgres -d menu_ranking -f upsert_menu_data.sql 2>&1 | tee $(LOG_DIR)/ingest_$(TIMESTAMP).log
	
	@echo "2️⃣ 사용자 선호도 분석 중 (user-service → analytics)..."
	@psql -h localhost -U postgres -d menu_ranking -c "$(shell cat upsert_menu_data.sql | grep -A 50 '사용자 선호도 데이터')" 2>&1 | tee -a $(LOG_DIR)/ingest_$(TIMESTAMP).log
	
	@echo "3️⃣ 외부 날씨 데이터 수집 중..."
	@$(PYTHON) collect_weather_data.py --cities=Seoul,Busan,Incheon 2>&1 | tee -a $(LOG_DIR)/ingest_$(TIMESTAMP).log
	
	@echo "4️⃣ AI 랭킹 성능 측정 중..."
	@cd ../ai-service && $(PYTHON) ai_ranking_results.py --save-to-db 2>&1 | tee -a $(LOG_DIR)/ingest_$(TIMESTAMP).log
	
	@echo "✅ PostgreSQL 데이터 파이프라인 완료: $(TIMESTAMP)"

quality-check: ## 데이터 품질 검사 실행
	@echo "🔍 데이터 품질 검사 시작..."
	@$(PYTHON) data_quality_monitor.py
	@echo "✅ 품질 검사 완료"

performance-test: ## AI 랭킹 성능 테스트 실행
	@echo "📊 AI 랭킹 성능 테스트 시작..."
	@cd ../ai-service && $(PYTHON) ai_ranking_results.py
	@echo "✅ 성능 테스트 완료"

upsert-test: ## PostgreSQL 멱등 적재 테스트
	@echo "🔄 PostgreSQL 멱등 적재 테스트 시작..."
	@echo "📊 테스트 전 데이터 수:"
	@psql -h localhost -U postgres -d menu_ranking -c "SELECT 'menu_features' as table_name, COUNT(*) FROM analytics.menu_features UNION SELECT 'user_preferences', COUNT(*) FROM analytics.user_preferences;"
	
	@echo "🔄 멱등 적재 실행..."
	@psql -h localhost -U postgres -d menu_ranking -f upsert_menu_data.sql
	
	@echo "📊 테스트 후 데이터 수:"
	@psql -h localhost -U postgres -d menu_ranking -c "SELECT 'menu_features' as table_name, COUNT(*) FROM analytics.menu_features UNION SELECT 'user_preferences', COUNT(*) FROM analytics.user_preferences;"
	@echo "✅ PostgreSQL 멱등 적재 테스트 완료"

db-connect: ## PostgreSQL 데이터베이스 연결 테스트
	@echo "🔌 PostgreSQL 연결 테스트..."
	@psql -h localhost -U postgres -d menu_ranking -c "SELECT current_database(), current_user, now();"
	@echo "✅ 데이터베이스 연결 성공"

schema-create: ## Analytics 스키마 생성
	@echo "🏗️ Analytics 스키마 생성 중..."
	@psql -h localhost -U postgres -d menu_ranking -c "CREATE SCHEMA IF NOT EXISTS analytics;"
	@echo "✅ Analytics 스키마 생성 완료"

extract-preferences: ## 주문 데이터 기반 사용자 선호도 추출 (복합)
	@echo "🧠 사용자 선호도 추출 시작..."
	@$(PYTHON) user_preference_collection.py
	@echo "✅ 선호도 추출 완료"

extract-simple-preferences: ## menucategory 기반 단순 선호도 추출
	@echo "🍜 단순 선호도 추출 시작 (menucategory 기반)..."
	@$(PYTHON) simple_preference_extraction.py
	@echo "✅ 단순 선호도 추출 완료"

preference-setup: ## 선호도 수집 시스템 전체 설정
	@echo "🎯 선호도 수집 시스템 설정 중..."
	@make schema-create
	@make extract-preferences
	@echo "✅ 선호도 수집 시스템 설정 완료"

full-pipeline: ingest quality-check performance-test ## 전체 파이프라인 + 품질검사 + 성능테스트
	@echo "🎯 전체 파이프라인 실행 완료!"
	@echo "📊 결과 요약:"
	@echo "   - 데이터 수집: ✅"
	@echo "   - 품질 검사: ✅"  
	@echo "   - 성능 테스트: ✅"
	@echo "📄 로그 위치: $(LOG_DIR)/ingest_$(TIMESTAMP).log"

clean: ## 임시 파일 및 로그 정리
	@echo "🧹 임시 파일 정리 중..."
	@rm -rf $(LOG_DIR)/*.log
	@rm -rf /tmp/data_quality_report.json
	@rm -rf /tmp/ai_ranking_performance.json
	@echo "✅ 정리 완료"

monitor: ## 실시간 모니터링 (5초마다 품질 체크)
	@echo "👁️ 실시간 모니터링 시작 (Ctrl+C로 중지)"
	@while true; do \
		clear; \
		echo "🕐 $(shell date '+%Y-%m-%d %H:%M:%S') - 실시간 모니터링"; \
		echo "================================"; \
		$(PYTHON) data_quality_monitor.py --brief; \
		sleep 5; \
	done

# 스케줄러 설정 (crontab 등록용)
schedule-daily: ## 매일 09:00 자동 실행 스케줄 등록
	@echo "⏰ 매일 09:00 자동 실행 스케줄 등록 중..."
	@(crontab -l 2>/dev/null; echo "0 9 * * * cd $(PWD) && make ingest >> $(LOG_DIR)/daily_$(shell date +%Y%m).log 2>&1") | crontab -
	@echo "✅ 스케줄 등록 완료: 매일 09:00에 데이터 파이프라인 자동 실행"
	@crontab -l | grep "make ingest"

remove-schedule: ## 자동 실행 스케줄 제거
	@echo "❌ 자동 실행 스케줄 제거 중..."
	@crontab -l | grep -v "make ingest" | crontab -
	@echo "✅ 스케줄 제거 완료"

# 개발용 명령어
dev-setup: ## 개발 환경 설정
	@echo "🛠️ 개발 환경 설정 중..."
	@pip install -r requirements.txt
	@mkdir -p $(DATA_DIR) $(LOG_DIR)
	@echo "✅ 개발 환경 설정 완료"

# 예시 실행 명령어 (발표용)
demo: ## 발표용 데모 실행 (빠른 버전)
	@echo "🎬 발표용 데모 시작!"
	@echo "================================"
	@echo "1️⃣ 데이터 파이프라인 실행..."
	@make ingest
	@echo ""
	@echo "2️⃣ 품질 검사..."
	@make quality-check
	@echo ""
	@echo "3️⃣ AI 성능 테스트..."
	@make performance-test
	@echo ""
	@echo "🎉 데모 완료! 결과를 확인해보세요."