-- 🍜 AI 메뉴 랭킹 시스템: PostgreSQL 기반 멱등 적재
-- 운영 DB → Analytics Schema 데이터 동기화

-- 1) 메뉴 데이터 멱등 적재 (store-service DB에서 추출)
INSERT INTO analytics.menu_features (
    menu_id,
    store_id,
    name,
    category,
    price,
    rating,
    review_count,
    tags,
    is_hot_food,
    weather_score,
    last_synced_at
)
SELECT 
    m.id as menu_id,
    m.store_id,
    m.name,
    m.category,
    m.price,
    COALESCE(AVG(r.rating), 0) as rating,
    COUNT(r.id) as review_count,
    ARRAY_AGG(DISTINCT t.tag_name) FILTER (WHERE t.tag_name IS NOT NULL) as tags,
    CASE 
        WHEN m.category IN ('한식', '찌개', '국물') THEN true
        WHEN m.category IN ('아이스크림', '음료', '샐러드') THEN false
        ELSE true
    END as is_hot_food,
    -- 날씨 점수 계산 (카테고리 기반)
    CASE 
        WHEN m.category IN ('한식', '찌개') THEN 0.8
        WHEN m.category IN ('burger', '치킨') THEN 0.6
        WHEN m.category IN ('아이스크림', '음료') THEN 0.3
        ELSE 0.5
    END as weather_score,
    NOW() as last_synced_at
FROM public.menus m
LEFT JOIN public.reviews r ON m.id = r.menu_id
LEFT JOIN public.menu_tags mt ON m.id = mt.menu_id  
LEFT JOIN public.tags t ON mt.tag_id = t.id
WHERE m.is_active = true
GROUP BY m.id, m.store_id, m.name, m.category, m.price
ON CONFLICT (menu_id) 
DO UPDATE SET
    store_id = EXCLUDED.store_id,
    name = EXCLUDED.name,
    category = EXCLUDED.category,
    price = EXCLUDED.price,
    rating = EXCLUDED.rating,
    review_count = EXCLUDED.review_count,
    tags = EXCLUDED.tags,
    is_hot_food = EXCLUDED.is_hot_food,
    weather_score = EXCLUDED.weather_score,
    last_synced_at = NOW();

-- 2) 사용자 선호도 데이터 멱등 적재 (user-service DB에서 추출)
INSERT INTO analytics.user_preferences (
    user_id,
    preference_category,
    preference_value,
    confidence_score,
    order_count,
    last_order_date,
    last_updated
)
SELECT 
    u.id as user_id,
    -- 주문 패턴 기반 선호도 추출
    CASE 
        WHEN COUNT(*) FILTER (WHERE m.category = '한식') > COUNT(*) * 0.4 THEN '한식'
        WHEN COUNT(*) FILTER (WHERE m.tags @> ARRAY['매운']) > COUNT(*) * 0.3 THEN '매운'
        WHEN COUNT(*) FILTER (WHERE m.tags @> ARRAY['고기']) > COUNT(*) * 0.3 THEN '고기'
        WHEN COUNT(*) FILTER (WHERE m.category = 'burger') > COUNT(*) * 0.2 THEN '양식'
        ELSE '일반'
    END as preference_category,
    -- 선호도 강도 (주문 빈도 기반)
    CASE 
        WHEN COUNT(*) >= 10 THEN 'high'
        WHEN COUNT(*) >= 5 THEN 'medium'
        ELSE 'low'
    END as preference_value,
    -- 신뢰도 점수 (주문 수 기반)
    LEAST(COUNT(*) * 0.1, 1.0) as confidence_score,
    COUNT(*) as order_count,
    MAX(o.created_at) as last_order_date,
    NOW() as last_updated
FROM public.users u
JOIN public.orders o ON u.id = o.user_id
JOIN public.order_items oi ON o.id = oi.order_id
JOIN analytics.menu_features m ON oi.menu_id = m.menu_id
WHERE o.status = 'COMPLETED'
  AND o.created_at >= NOW() - INTERVAL '90 days'  -- 최근 3개월
GROUP BY u.id
HAVING COUNT(*) >= 3  -- 최소 3번 주문한 사용자만
ON CONFLICT (user_id) 
DO UPDATE SET
    preference_category = EXCLUDED.preference_category,
    preference_value = EXCLUDED.preference_value,
    confidence_score = EXCLUDED.confidence_score,
    order_count = EXCLUDED.order_count,
    last_order_date = EXCLUDED.last_order_date,
    last_updated = NOW();

-- 3) 날씨 데이터 멱등 적재 (외부 API → PostgreSQL)
INSERT INTO analytics.weather_data (
    city,
    weather_date,
    temperature,
    weather_condition,
    humidity,
    wind_speed,
    created_at
)
VALUES (
    $1,  -- city
    DATE_TRUNC('hour', $2),  -- weather_date (시간 단위 반올림)
    $3,  -- temperature
    $4,  -- weather_condition
    $5,  -- humidity
    $6,  -- wind_speed
    NOW()
)
ON CONFLICT (city, weather_date)
DO UPDATE SET
    temperature = EXCLUDED.temperature,
    weather_condition = EXCLUDED.weather_condition,
    humidity = EXCLUDED.humidity,
    wind_speed = EXCLUDED.wind_speed,
    created_at = NOW();

-- 4) AI 랭킹 성능 데이터 적재
INSERT INTO analytics.ranking_performance (
    test_scenario,
    city,
    weather_condition,
    temperature,
    user_preference,
    ndcg5_score,
    hit5_score,
    response_time_ms,
    total_menus,
    measured_at
)
VALUES (
    $1, $2, $3, $4, $5, $6, $7, $8, $9, NOW()
)
ON CONFLICT (test_scenario, measured_at::date)
DO UPDATE SET
    ndcg5_score = EXCLUDED.ndcg5_score,
    hit5_score = EXCLUDED.hit5_score,
    response_time_ms = EXCLUDED.response_time_ms,
    total_menus = EXCLUDED.total_menus,
    measured_at = NOW();

-- 실행 예시 커맨드:
-- psql -d menu_ranking -f upsert_menu_data.sql
-- make sync-postgres-data
