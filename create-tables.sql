-- Create all required tables for Transformer Thermal Inspection System
-- Database: trasformer_inspection_db

-- Drop tables if they exist (to start fresh)
DROP TABLE IF EXISTS analysis_result CASCADE;
DROP TABLE IF EXISTS image_inspect CASCADE;
DROP TABLE IF EXISTS inspection_records CASCADE;
DROP TABLE IF EXISTS transformer_records CASCADE;
DROP SEQUENCE IF EXISTS inspection_sequence;

-- Create transformer_records table
CREATE TABLE transformer_records (
    id BIGSERIAL PRIMARY KEY,
    transformer_no VARCHAR(255) UNIQUE NOT NULL,
    pole_no VARCHAR(255),
    regions VARCHAR(255),
    type VARCHAR(255),
    location VARCHAR(255)
);

-- Create inspection_records table
CREATE TABLE inspection_records (
    id BIGSERIAL PRIMARY KEY,
    inspection_no VARCHAR(255) UNIQUE NOT NULL,
    transformer_no VARCHAR(255) NOT NULL,
    branch VARCHAR(255),
    date_of_inspection VARCHAR(255),
    time VARCHAR(255),
    maintenance_date VARCHAR(255),
    status VARCHAR(255)
);

-- Create image_inspect table
CREATE TABLE image_inspect (
    id BIGSERIAL PRIMARY KEY,
    transformer_no VARCHAR(255),
    inspection_no VARCHAR(255),
    image_type VARCHAR(50) NOT NULL,
    image_data BYTEA,
    upload_date VARCHAR(255),
    weather_condition VARCHAR(255),
    status VARCHAR(50)
);

-- Create analysis_result table
CREATE TABLE analysis_result (
    id BIGSERIAL PRIMARY KEY,
    inspection_no VARCHAR(255) NOT NULL UNIQUE,
    transformer_no VARCHAR(255) NOT NULL,
    annotated_image_data BYTEA, -- Allow NULL for failed analyses
    analysis_result_json TEXT,
    analysis_status VARCHAR(50) NOT NULL,
    analysis_date TIMESTAMP NOT NULL,
    processing_time_ms BIGINT,
    error_message TEXT
);

-- Create sequence for inspection numbers
CREATE SEQUENCE inspection_sequence START 1;

-- Create indexes for better performance
CREATE INDEX idx_image_inspect_transformer_no ON image_inspect(transformer_no);
CREATE INDEX idx_image_inspect_inspection_no ON image_inspect(inspection_no);
CREATE INDEX idx_image_inspect_type ON image_inspect(image_type);
CREATE INDEX idx_inspection_records_transformer_no ON inspection_records(transformer_no);
CREATE INDEX idx_inspection_records_inspection_no ON inspection_records(inspection_no);
CREATE INDEX idx_analysis_result_inspection_no ON analysis_result(inspection_no);
CREATE INDEX idx_analysis_result_transformer_no ON analysis_result(transformer_no);

-- Display table creation status
SELECT 'Tables created successfully!' AS status;
