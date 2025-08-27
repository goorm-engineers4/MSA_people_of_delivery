#!/usr/bin/env python3
"""
데이터 파이프라인 - 멱등 적재 스니펫
Source(CSV/DB) → Staging(Postgres) → Transform
"""

import pandas as pd
import psycopg2
from psycopg2.extras import execute_values
import logging
from datetime import datetime, timezone
import os
from typing import Dict, Any, List

# 로깅 설정
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class DataPipeline:
    def __init__(self):
        self.db_config = {
            'host': os.getenv('DB_HOST', 'localhost'),
            'port': os.getenv('DB_PORT', 5432),
            'database': os.getenv('DB_NAME', 'msa_data_warehouse'),
            'user': os.getenv('DB_USER', 'postgres'),
            'password': os.getenv('DB_PASSWORD', 'password')
        }
    
    def get_connection(self):
        """PostgreSQL 연결 생성"""
        return psycopg2.connect(**self.db_config)
    
    def upsert_orders(self, orders_df: pd.DataFrame) -> int:
        """
        주문 데이터 멱등 적재 - INSERT ... ON CONFLICT DO UPDATE
        """
        conn = self.get_connection()
        cursor = conn.cursor()
        
        try:
            # 멱등 적재 SQL
            upsert_sql = """
            INSERT INTO staging.orders (
                order_id, user_id, store_id, total_amount, 
                order_status, created_at, updated_at, ingestion_time
            ) VALUES %s
            ON CONFLICT (order_id) DO UPDATE SET
                total_amount = EXCLUDED.total_amount,
                order_status = EXCLUDED.order_status,
                updated_at = EXCLUDED.updated_at,
                ingestion_time = EXCLUDED.ingestion_time
            """
            
            # 데이터 준비
            current_time = datetime.now(timezone.utc)
            data_tuples = []
            
            for _, row in orders_df.iterrows():
                data_tuples.append((
                    row['order_id'],
                    row['user_id'], 
                    row['store_id'],
                    row['total_amount'],
                    row['order_status'],
                    row['created_at'],
                    row.get('updated_at', current_time),
                    current_time
                ))
            
            # 배치 실행
            execute_values(cursor, upsert_sql, data_tuples)
            conn.commit()
            
            affected_rows = cursor.rowcount
            logger.info(f"Orders 테이블 upsert 완료: {affected_rows}건")
            return affected_rows
            
        except Exception as e:
            conn.rollback()
            logger.error(f"Orders upsert 실패: {e}")
            raise
        finally:
            cursor.close()
            conn.close()
    
    def upsert_menu_items(self, menu_df: pd.DataFrame) -> int:
        """
        메뉴 아이템 데이터 멱등 적재
        """
        conn = self.get_connection()
        cursor = conn.cursor()
        
        try:
            upsert_sql = """
            INSERT INTO staging.menu_items (
                menu_id, store_id, name, description, price, 
                category, is_available, created_at, updated_at, ingestion_time
            ) VALUES %s
            ON CONFLICT (menu_id) DO UPDATE SET
                name = EXCLUDED.name,
                description = EXCLUDED.description,
                price = EXCLUDED.price,
                category = EXCLUDED.category,
                is_available = EXCLUDED.is_available,
                updated_at = EXCLUDED.updated_at,
                ingestion_time = EXCLUDED.ingestion_time
            """
            
            current_time = datetime.now(timezone.utc)
            data_tuples = []
            
            for _, row in menu_df.iterrows():
                data_tuples.append((
                    row['menu_id'],
                    row['store_id'],
                    row['name'],
                    row.get('description', ''),
                    row['price'],
                    row.get('category', 'etc'),
                    row.get('is_available', True),
                    row.get('created_at', current_time),
                    row.get('updated_at', current_time),
                    current_time
                ))
            
            execute_values(cursor, upsert_sql, data_tuples)
            conn.commit()
            
            affected_rows = cursor.rowcount
            logger.info(f"Menu Items 테이블 upsert 완료: {affected_rows}건")
            return affected_rows
            
        except Exception as e:
            conn.rollback()
            logger.error(f"Menu Items upsert 실패: {e}")
            raise
        finally:
            cursor.close()
            conn.close()
    
    def merge_reviews(self, reviews_df: pd.DataFrame) -> int:
        """
        리뷰 데이터 MERGE 방식 멱등 적재
        """
        conn = self.get_connection()
        cursor = conn.cursor()
        
        try:
            # MERGE 스타일 upsert (PostgreSQL 15+ 또는 CTE 사용)
            merge_sql = """
            WITH source_data AS (
                SELECT * FROM (VALUES %s) AS t(
                    review_id, order_id, user_id, store_id, rating, 
                    comment, created_at, updated_at, ingestion_time
                )
            ),
            upserted AS (
                INSERT INTO staging.reviews 
                SELECT * FROM source_data
                ON CONFLICT (review_id) DO UPDATE SET
                    rating = EXCLUDED.rating,
                    comment = EXCLUDED.comment,
                    updated_at = EXCLUDED.updated_at,
                    ingestion_time = EXCLUDED.ingestion_time
                RETURNING *
            )
            SELECT COUNT(*) FROM upserted;
            """
            
            current_time = datetime.now(timezone.utc)
            data_tuples = []
            
            for _, row in reviews_df.iterrows():
                data_tuples.append((
                    row['review_id'],
                    row['order_id'],
                    row['user_id'],
                    row['store_id'],
                    row['rating'],
                    row.get('comment', ''),
                    row.get('created_at', current_time),
                    row.get('updated_at', current_time),
                    current_time
                ))
            
            execute_values(cursor, merge_sql, data_tuples)
            result = cursor.fetchone()
            conn.commit()
            
            affected_rows = result[0] if result else len(data_tuples)
            logger.info(f"Reviews 테이블 merge 완료: {affected_rows}건")
            return affected_rows
            
        except Exception as e:
            conn.rollback()
            logger.error(f"Reviews merge 실패: {e}")
            raise
        finally:
            cursor.close()
            conn.close()

def main():
    """메인 실행 함수"""
    pipeline = DataPipeline()
    
    # 샘플 데이터 로드 (실제로는 CSV 파일이나 DB에서)
    orders_df = pd.DataFrame({
        'order_id': [1, 2, 3],
        'user_id': [101, 102, 103],
        'store_id': [201, 202, 203],
        'total_amount': [25000, 18000, 32000],
        'order_status': ['completed', 'pending', 'completed'],
        'created_at': pd.to_datetime(['2024-01-15 12:00:00', '2024-01-15 13:30:00', '2024-01-15 14:15:00'])
    })
    
    menu_df = pd.DataFrame({
        'menu_id': [1001, 1002, 1003],
        'store_id': [201, 201, 202],
        'name': ['치킨버거', '새우버거', '불고기피자'],
        'description': ['바삭한 치킨패티', '통새우 패티', '한우 불고기'],
        'price': [8000, 9000, 18000],
        'category': ['burger', 'burger', 'pizza']
    })
    
    reviews_df = pd.DataFrame({
        'review_id': [5001, 5002, 5003],
        'order_id': [1, 2, 3],
        'user_id': [101, 102, 103],
        'store_id': [201, 202, 203],
        'rating': [4.5, 5.0, 4.0],
        'comment': ['맛있어요!', '최고입니다', '괜찮네요']
    })
    
    # 멱등 적재 실행
    try:
        pipeline.upsert_orders(orders_df)
        pipeline.upsert_menu_items(menu_df)
        pipeline.merge_reviews(reviews_df)
        logger.info("데이터 파이프라인 적재 완료")
    except Exception as e:
        logger.error(f"파이프라인 실행 실패: {e}")

if __name__ == "__main__":
    main()

