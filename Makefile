.PHONY: help build up down logs clean dev-up dev-down test

# Default target
help:
	@echo "Available commands:"
	@echo "  make build         - Build the Docker images"
	@echo "  make up            - Start production containers"
	@echo "  make down          - Stop production containers"
	@echo "  make logs          - View application logs"
	@echo "  make clean         - Remove containers and volumes"
	@echo "  make dev-up        - Start development containers with hot reload"
	@echo "  make dev-down      - Stop development containers"
	@echo "  make dev-logs      - View development logs"
	@echo "  make test          - Run tests"
	@echo "  make shell         - Open shell in running container"
	@echo "  make db-shell      - Open PostgreSQL shell"
	@echo "  make restart       - Restart the application"

# Production commands
build:
	@echo "Building Docker images..."
	docker-compose build

up:
	@echo "Starting production containers..."
	docker-compose up -d
	@echo "Application starting at http://localhost:5509"
	@echo "Health check: http://localhost:5509/transformer-thermal-inspection/actuator/health"

down:
	@echo "Stopping production containers..."
	docker-compose down

logs:
	docker-compose logs -f app

restart:
	@echo "Restarting application..."
	docker-compose restart app

# Development commands
dev-up:
	@echo "Starting development containers..."
	docker-compose -f docker-compose.dev.yml up -d
	@echo "Application starting at http://localhost:5509"
	@echo "Debug port available at localhost:5005"

dev-down:
	@echo "Stopping development containers..."
	docker-compose -f docker-compose.dev.yml down

dev-logs:
	docker-compose -f docker-compose.dev.yml logs -f app

dev-build:
	@echo "Building development images..."
	docker-compose -f docker-compose.dev.yml build

# Database commands
db-shell:
	@echo "Opening PostgreSQL shell..."
	docker-compose exec postgres psql -U postgres -d trasformer_inspection_db

db-backup:
	@echo "Creating database backup..."
	docker-compose exec postgres pg_dump -U postgres trasformer_inspection_db > backup_$$(date +%Y%m%d_%H%M%S).sql

db-restore:
	@echo "Restoring database from backup..."
	@read -p "Enter backup file path: " BACKUP_FILE; \
	docker-compose exec -T postgres psql -U postgres trasformer_inspection_db < $$BACKUP_FILE

# Utility commands
shell:
	@echo "Opening shell in application container..."
	docker-compose exec app sh

test:
	@echo "Running tests..."
	cd app && ./mvnw test

clean:
	@echo "Cleaning up containers and volumes..."
	docker-compose down -v
	docker-compose -f docker-compose.dev.yml down -v
	@echo "Removing dangling images..."
	docker image prune -f

status:
	@echo "Container status:"
	docker-compose ps
	@echo ""
	@echo "Development containers:"
	docker-compose -f docker-compose.dev.yml ps

# Build and run
run: build up

dev-run: dev-build dev-up
