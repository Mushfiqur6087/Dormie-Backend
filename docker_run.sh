#!/bin/bash

echo "🚀 Starting HMS (Hall Management System) with Docker Compose..."

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "❌ Error: .env file not found!"
    echo "Please copy .env.example to .env and configure your environment variables:"
    echo "   cp .env.example .env"
    echo "   # Then edit .env with your actual configuration"
    exit 1
fi

# Build and start all services
docker compose up --build -d

echo ""
echo "⏳ Waiting for services to be healthy..."

# Wait for backend to be healthy
for i in {1..30}; do
  if curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
    echo "✅ HMS Backend is healthy and ready!"
    break
  fi
  echo "Waiting for backend to be ready... (attempt $i/30)"
  sleep 5
done

if ! curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
  echo "❌ Backend failed to become healthy within 150 seconds"
  echo "Check logs with: docker compose logs backend"
  exit 1
fi

echo ""
echo "🎉 HMS is successfully running!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📍 Backend URL:      http://localhost:8080"
echo "🔍 Health Check:     http://localhost:8080/actuator/health"
echo "🗄️  Database:        localhost:5433 (HMS/postgres/postgres)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📋 Useful commands:"
echo "   View logs:        docker compose logs -f"
echo "   View backend logs: docker compose logs -f backend"
echo "   View DB logs:     docker compose logs -f db"
echo "   Stop services:    docker compose down"
echo "   Restart:          docker compose restart"
echo "   Clean rebuild:    docker compose down && docker compose up --build"
