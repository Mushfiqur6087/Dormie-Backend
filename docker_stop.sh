#!/bin/bash

echo "🛑 Stopping HMS (Hall Management System) services..."

# Stop all services defined in docker-compose.yml
docker compose down

echo ""
echo "✅ All HMS services have been stopped successfully!"
echo ""
echo "📋 Additional cleanup options:"
echo "   Remove volumes:   docker compose down -v"
echo "   Remove images:    docker compose down --rmi all"
echo "   Full cleanup:     docker compose down -v --rmi all --remove-orphans"
