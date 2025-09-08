-- Database Optimization Scripts for Transformer Inspection System

-- 1. Add indexes for frequently queried columns
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_transformer_records_transformer_no 
ON transformer_records (transformer_no);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_transformer_records_regions 
ON transformer_records (regions);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_transformer_records_type 
ON transformer_records (type);

-- 2. Add indexes for inspection records (assuming similar structure)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_inspection_records_transformer_no 
ON inspection_records (transformer_no);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_inspection_records_date 
ON inspection_records (date_of_inspection);

-- 3. Add composite indexes for common filter combinations
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_transformer_records_region_type 
ON transformer_records (regions, type);

-- 4. Add unique constraint if transformer_no should be unique
-- ALTER TABLE transformer_records ADD CONSTRAINT uk_transformer_no UNIQUE (transformer_no);

-- 5. Vacuum and analyze tables for optimal performance
VACUUM ANALYZE transformer_records;
VACUUM ANALYZE inspection_records;
