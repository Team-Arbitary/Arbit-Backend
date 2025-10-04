#!/bin/bash

# Startup script for Transformer Inspection Backend
set -e

echo "🚀 Starting Transformer Inspection Backend..."
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    echo "   Visit: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    echo "   Visit: https://docs.docker.com/compose/install/"
    exit 1
fi

# Check if .env file exists, if not create a default one
if [ ! -f .env ]; then
    echo "📝 Creating default .env file..."
    cat > .env << EOF
# Database Configuration
POSTGRES_DB=trasformer_inspection_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234

# Application Configuration
SERVER_PORT=5509
SPRING_PROFILES_ACTIVE=prod

# Analysis API (optional)
ANALYSIS_API_URL=http://localhost:8000
EOF
    echo "✅ Created .env file with default values"
fi

# Parse command line arguments
MODE="production"
if [ "$1" = "dev" ] || [ "$1" = "development" ]; then
    MODE="development"
fi

if [ "$MODE" = "development" ]; then
    echo "🔧 Starting in DEVELOPMENT mode..."
    echo "   - Hot reload enabled"
    echo "   - Debug port: 5005"
    docker-compose -f docker-compose.dev.yml up -d
else
    echo "🏭 Starting in PRODUCTION mode..."
    docker-compose up -d
fi

# Wait for application to be healthy
echo ""
echo "⏳ Waiting for application to start..."
sleep 10

# Check if application is running
if docker-compose ps | grep -q "Up"; then
    echo ""
    echo "✅ Application started successfully!"
    echo ""
    echo "📍 Access the application:"
    echo "   🌐 Application: http://localhost:5509"
    echo "   ❤️  Health Check: http://localhost:5509/transformer-thermal-inspection/actuator/health"
    echo ""
    echo "📊 Useful commands:"
    echo "   View logs:     docker-compose logs -f app"
    echo "   Stop:          docker-compose down"
    echo "   Restart:       docker-compose restart app"
    if [ "$MODE" = "development" ]; then
        echo "   Debug:         Connect to localhost:5005"
    fi
    echo ""
else
    echo "❌ Application failed to start. Check logs with:"
    echo "   docker-compose logs app"
    exit 1
fi
