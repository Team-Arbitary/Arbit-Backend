<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
</p>

<h1 align="center">🔌 Arbit Backend</h1>

<p align="center">
  <strong>Transformer Thermal Inspection Management System - Backend API</strong>
</p>

<p align="center">
  A robust Spring Boot RESTful API for managing transformer thermal inspections, image analysis, and maintenance records with comprehensive versioning support.
</p>

<p align="center">
  <a href="#-features">Features</a> •
  <a href="#-quick-start">Quick Start</a> •
  <a href="#-database-schema">Database Schema</a> •
  <a href="#-api-documentation">API Docs</a> •
  <a href="#-form-generation--report-system">Reports</a> •
  <a href="#-team">Team</a>
</p>

---

## 👥 Team Arbitary

| Name | Role |
|------|------|
| **Yasiru Basnayake** | Full Stack Developer |
| **Kumal Loneth** | Backend Developer |
| **Dasuni Dissanayake** | Frontend Developer |
| **Hasitha Gallella** | ML/AI Engineer |

---

## ✨ Features

- **🔧 Transformer Management** - Full CRUD operations for transformer records
- **🔍 Inspection Management** - Track and manage thermal inspections
- **📷 Image Management** - Upload, store, and retrieve baseline/thermal images
- **🤖 ML Analysis Integration** - Automated anomaly detection results storage
- **📋 Report Generation** - Maintenance records and thermal inspection reports with versioning
- **📜 Version History** - Complete audit trail for all reports with restore capability
- **🔐 JWT Authentication** - Secure API access with role-based authorization
- **🐳 Docker Support** - Easy deployment with Docker Compose

---

## 🚀 Quick Start

### Option 1: Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/Team-Arbitary/Arbit-Backend.git
cd Arbit-Backend

# Start with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f app
```

**Access the API at:** `http://localhost:5509/transformer-thermal-inspection`

### Option 2: Local Development

#### Prerequisites
- **JDK 21**
- **Maven 3.9+**
- **PostgreSQL 14+**

#### Database Setup

```sql
-- Create database
CREATE DATABASE trasformer_inspection_db;

-- Create user (if needed)
CREATE USER postgres WITH PASSWORD '1234';
GRANT ALL PRIVILEGES ON DATABASE trasformer_inspection_db TO postgres;
```

#### Run the Application

```bash
cd app
mvn clean install
mvn spring-boot:run
```

---

## 🗄️ Database Schema

### Entity Relationship Diagram

```
┌─────────────────────┐       ┌─────────────────────────┐
│ transformer_records │       │   inspection_records    │
├─────────────────────┤       ├─────────────────────────┤
│ id (PK)             │◄──────│ id (PK)                 │
│ transformer_no (UK) │       │ inspection_no (UK)      │
│ pole_no             │       │ transformer_no (FK)     │
│ regions             │       │ branch                  │
│ type                │       │ date_of_inspection      │
│ location            │       │ time                    │
└─────────────────────┘       │ maintenance_date        │
                              │ status                  │
                              └───────────┬─────────────┘
                                          │
          ┌───────────────────────────────┼───────────────────────────────┐
          │                               │                               │
          ▼                               ▼                               ▼
┌─────────────────────┐     ┌─────────────────────────┐     ┌─────────────────────────────┐
│   image_inspect     │     │   maintenance_records   │     │ thermal_inspection_reports  │
├─────────────────────┤     ├─────────────────────────┤     ├─────────────────────────────┤
│ id (PK)             │     │ id (PK)                 │     │ id (PK)                     │
│ transformer_no      │     │ inspection_id (FK)      │     │ inspection_id (FK)          │
│ inspection_no       │     │ inspector_name          │     │ transformer_no              │
│ image_type          │     │ status                  │     │ report_data (JSON)          │
│ image_data (BYTEA)  │     │ voltage_reading         │     │ version                     │
│ upload_date         │     │ current_reading         │     │ is_current                  │
│ weather_condition   │     │ recommended_action      │     │ created_by                  │
│ status              │     │ remarks                 │     │ created_at                  │
└─────────────────────┘     │ report_data (JSON)      │     │ updated_at                  │
                            │ version                 │     └─────────────────────────────┘
                            │ is_current              │
                            │ start_time              │
                            │ completion_time         │
┌─────────────────────┐     │ supervised_by           │
│   analysis_result   │     │ tech_i, tech_ii, tech_iii│
├─────────────────────┤     │ helpers                 │
│ id (PK)             │     │ inspected_by            │
│ inspection_no (UK)  │     │ rectified_by            │
│ transformer_no      │     │ re_inspected_by         │
│ annotated_image_data│     │ css                     │
│ analysis_result_json│     │ created_at              │
│ analysis_status     │     │ updated_at              │
│ analysis_date       │     └─────────────────────────┘
│ processing_time_ms  │
│ error_message       │
└─────────────────────┘
```

### Table Descriptions

| Table | Description |
|-------|-------------|
| `transformer_records` | Stores transformer equipment information (ID, location, type, pole number) |
| `inspection_records` | Tracks inspection events linked to transformers (dates, status, branch) |
| `image_inspect` | Stores baseline and thermal images as binary data (BYTEA) |
| `analysis_result` | ML analysis results including annotated images and JSON detection data |
| `maintenance_records` | Detailed maintenance reports with full versioning support |
| `thermal_inspection_reports` | Thermal inspection forms with versioning for audit trail |

---

## 📋 Form Generation & Report System

### How It Works

The system implements a **versioned report management** approach for generating and storing maintenance records and thermal inspection reports.

#### 1. Report Creation Flow

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   User fills    │────▶│  Frontend sends │────▶│ Backend creates │
│   form in UI    │     │  JSON payload   │     │  new version    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                                        │
                                                        ▼
                                               ┌─────────────────┐
                                               │ Mark previous   │
                                               │ as non-current  │
                                               └─────────────────┘
```

#### 2. Versioning Mechanism

- **Each save creates a new version** - Previous versions are preserved for audit
- **`is_current` flag** - Only one version is marked as current at any time
- **`version` counter** - Incremental version number per inspection
- **Full audit trail** - Complete history of all changes with timestamps

```java
// Backend versioning logic:
1. Query max version for inspection_id
2. Mark all existing records as is_current = false
3. Create new record with version = max + 1, is_current = true
4. Return saved record with new version number
```

#### 3. Report Data Structure

Reports store comprehensive form data as JSON in the `report_data` field:

```json
{
  "startTime": "08:00",
  "completionTime": "16:00",
  "supervisedBy": "John Smith",
  "gangComposition": {
    "tech1": "Technician A",
    "tech2": "Technician B",
    "tech3": "Technician C",
    "helpers": "Helper 1, Helper 2"
  },
  "workDataSheet": {
    "gangLeader": "Leader Name",
    "kva": "100",
    "make": "ABB",
    "tapPosition": "3",
    "earthResistance": "0.5"
  },
  "checklist": {
    "transformerCondition": true,
    "bushingCondition": true,
    "oilLevelChecked": true,
    "connectionsTightened": true
  },
  "findings": {
    "severity": "critical",
    "description": "Hotspot detected at bushing",
    "recommendations": ["Replace insulation", "Schedule maintenance"]
  }
}
```

#### 4. Version History & Restore

Users can view complete version history and restore any previous version:

```
GET  /api/maintenance-records/history/{inspectionId}           → Get all versions
GET  /api/maintenance-records/version/{inspectionId}/{version} → Get specific version
POST /api/maintenance-records/restore/{inspectionId}/{version} → Restore old version as new
```

---

## 📖 API Documentation

### Base URL
```
http://localhost:5509/transformer-thermal-inspection
```

### Authentication

```bash
# Login to get JWT token
POST /api/users/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}

# Response
{
  "jwt": "eyJhbGciOiJIUzI1NiIs..."
}

# Use token in subsequent requests
Authorization: Bearer <jwt_token>
```

### API Endpoints

#### Transformer Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/transformer-management/create` | Create new transformer record |
| `GET` | `/transformer-management/view/{id}` | Get transformer by ID |
| `GET` | `/transformer-management/view-all` | Get all transformers |
| `PUT` | `/transformer-management/update` | Update transformer details |
| `DELETE` | `/transformer-management/delete/{id}` | Delete transformer |
| `POST` | `/transformer-management/filter` | Filter transformers |

#### Inspection Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/inspection-management/create` | Create new inspection |
| `GET` | `/inspection-management/view/{id}` | Get inspection by ID |
| `GET` | `/inspection-management/transformer/{transformerNo}` | Get inspections by transformer |
| `GET` | `/inspection-management/view-all` | Get all inspections |
| `PUT` | `/inspection-management/update` | Update inspection |
| `DELETE` | `/inspection-management/delete/{id}` | Delete inspection |

#### Image Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/image-inspection-management/upload` | Upload baseline or thermal image |
| `GET` | `/image-inspection-management/baseline/{transformerNo}` | Get baseline image |
| `GET` | `/image-inspection-management/thermal/{inspectionNo}` | Get thermal image |
| `PUT` | `/image-inspection-management/baseline/{transformerNo}` | Update baseline image |
| `DELETE` | `/image-inspection-management/thermal/{inspectionNo}` | Delete thermal image |

#### Maintenance Records (with Versioning)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/maintenance-records` | Save record (creates new version) |
| `GET` | `/api/maintenance-records/{id}` | Get record by ID |
| `GET` | `/api/maintenance-records/inspection/{inspectionId}` | Get current record |
| `GET` | `/api/maintenance-records/history/{inspectionId}` | Get version history |
| `GET` | `/api/maintenance-records/version/{inspectionId}/{version}` | Get specific version |
| `POST` | `/api/maintenance-records/restore/{inspectionId}/{version}` | Restore version |

#### Thermal Inspection Reports (with Versioning)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/thermal-inspection-reports` | Save report (creates new version) |
| `GET` | `/api/thermal-inspection-reports/inspection/{inspectionId}` | Get current report |
| `GET` | `/api/thermal-inspection-reports/history/{inspectionId}` | Get version history |
| `POST` | `/api/thermal-inspection-reports/restore/{inspectionId}/{version}` | Restore version |

---

## ⚙️ Configuration

### Application Properties (`application.yml`)

```yaml
server:
  port: 5509
  servlet:
    context-path: /transformer-thermal-inspection

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/trasformer_inspection_db
    username: postgres
    password: 1234
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

jwt:
  secret: your-256-bit-secret-key
  expiration: 86400000  # 24 hours in milliseconds
```

---

## 🐳 Docker Deployment

### Using Makefile

```bash
make up          # Start production containers
make dev-up      # Start development with hot reload
make logs        # View application logs
make down        # Stop all containers
make clean       # Remove containers and volumes
```

### Docker Compose Services

| Service | Port | Description |
|---------|------|-------------|
| `app` | 5509 | Spring Boot application |
| `db` | 5432 | PostgreSQL database |

---

## 🔧 Development

### Project Structure

```
Arbit-Backend/
├── app/
│   ├── src/main/java/com/uom/Software_design_competition/
│   │   ├── application/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── transport/       # Request/Response DTOs
│   │   │   └── util/            # Utilities
│   │   └── domain/
│   │       ├── entity/          # JPA entities
│   │       ├── repository/      # Spring Data repositories
│   │       └── service/         # Business logic
│   └── src/main/resources/
│       ├── application.yml      # Main config
│       └── application.properties
├── docker-compose.yml
├── Dockerfile
├── Makefile
└── create-tables.sql            # Database schema
```

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  Made with ❤️ by <strong>Team Arbitary</strong>
</p>
