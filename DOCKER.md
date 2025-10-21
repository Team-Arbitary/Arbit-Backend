# Docker Setup Guide

This guide explains how to run the Transformer Thermal Inspection application using Docker.

## Prerequisites

- Docker Engine 20.10+ or Docker Desktop
- Docker Compose 2.0+

## Quick Start

### Option 1: Using Docker Compose (Recommended)

This will start both the Spring Boot application and PostgreSQL database:

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove volumes (deletes database data)
docker-compose down -v
```

The application will be available at: http://localhost:5509

### Overriding the Application Port

If port 5509 is already in use on your machine, you can start the application on a different port:

- With the startup script (overrides SERVER_PORT):

```bash
./start.sh 5600    # Start in production mode with app bound to port 5600
./start.sh dev 5601 # Start dev compose and run app on port 5601
```

- With docker run:

```bash
docker run -d -p 5600:5509 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/trasformer_inspection_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=1234 \
  transformer-inspection-app
```

### Option 2: Using Docker Only

If you already have PostgreSQL running locally:

```bash
# Build the image
docker build -t transformer-inspection-app .

# Run the container
docker run -d \
  --name transformer-inspection \
  -p 5509:5509 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/trasformer_inspection_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=1234 \
  transformer-inspection-app

# View logs
docker logs -f transformer-inspection

# Stop the container
docker stop transformer-inspection
docker rm transformer-inspection
```

## Configuration

### Environment Variables

You can override application properties using environment variables:

| Variable                     | Default                                                     | Description             |
| ---------------------------- | ----------------------------------------------------------- | ----------------------- |
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://localhost:5432/trasformer_inspection_db` | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres`                                                  | Database username       |
| `SPRING_DATASOURCE_PASSWORD` | `1234`                                                      | Database password       |
| `SERVER_PORT`                | `5509`                                                      | Application port        |
| `ANALYSIS_API_URL`           | `http://192.248.10.121:8082`                                | Python analysis API URL |

Example with custom database:

```bash
docker run -d \
  --name transformer-inspection \
  -p 5509:5509 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db-server:5432/mydb \
  -e SPRING_DATASOURCE_USERNAME=myuser \
  -e SPRING_DATASOURCE_PASSWORD=mypassword \
  transformer-inspection-app
```

## Health Check

The application includes health check endpoints:

```bash
# Check application health
curl http://localhost:5509/transformer-thermal-inspection/actuator/health

# Check with Docker
docker inspect --format='{{.State.Health.Status}}' transformer-inspection-app
```

## Development

### Running Locally Without Docker

```bash
cd app
./mvnw spring-boot:run
```

### Building for Production

```bash
# Build optimized image
docker build -t transformer-inspection-app:latest .

# With build arguments for JVM tuning
docker build \
  --build-arg JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC" \
  -t transformer-inspection-app:production .
```

## Troubleshooting

### Application won't start

1. Check logs:

   ```bash
   docker-compose logs app
   ```

2. Verify database is running:

   ```bash
   docker-compose ps postgres
   ```

3. Check database connectivity:
   ```bash
   docker-compose exec postgres psql -U postgres -d trasformer_inspection_db -c '\dt'
   ```

### Database connection issues

If using `host.docker.internal` doesn't work on Linux:

```bash
docker run -d \
  --name transformer-inspection \
  --network host \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/trasformer_inspection_db \
  transformer-inspection-app
```

### Port already in use

Change the port mapping:

```bash
# Use port 8080 instead of 5509
docker run -d -p 8080:5509 transformer-inspection-app
```

If you see the application failing to start because port 5509 is already in use, find and stop the process:

```bash
# Find process listening on port 5509
lsof -i TCP:5509 -sTCP:LISTEN

# Kill the process (replace <PID> with the PID from the list)
kill -9 <PID>
# OR more gracefully:
kill <PID>
```

Alternatively start the app on another port using the startup script or docker commands (see "Overriding the Application Port" section above).

## Production Deployment

For production environments, consider:

1. Using Docker secrets for passwords
2. Setting up a reverse proxy (nginx/traefik)
3. Using an external PostgreSQL database
4. Implementing proper logging and monitoring
5. Setting resource limits

Example docker-compose for production:

```yaml
services:
  app:
    image: transformer-inspection-app:latest
    deploy:
      resources:
        limits:
          cpus: "2"
          memory: 2G
        reservations:
          cpus: "1"
          memory: 1G
    environment:
      SPRING_PROFILES_ACTIVE: production
      JAVA_OPTS: "-Xms1g -Xmx2g -XX:+UseG1GC"
```

## Additional Resources

- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- Application API Documentation: http://localhost:5509/transformer-thermal-inspection/actuator
