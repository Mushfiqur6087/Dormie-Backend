#!/bin/bash

echo "🔍 Inspecting Docker Container Environment on VM"
echo "================================================"

echo "1. Check what JWT_SECRET is actually set to in the container:"
echo "docker exec hms-backend env | grep JWT_SECRET"
echo ""

echo "2. Check all environment variables in the container:"
echo "docker exec hms-backend env | grep -E '(SPRING|JWT|POSTGRES)'"
echo ""

echo "3. Check the actual .env file content on VM:"
echo "cat .env | grep JWT"
echo ""

echo "4. Check if the .env file is properly mounted:"
echo "docker exec hms-backend ls -la /app/"
echo ""

echo "5. Check what application.properties looks like inside container:"
echo "docker exec hms-backend cat /app/application.properties"
echo ""

echo "6. Check the Java process environment:"
echo "docker exec hms-backend printenv | sort"
echo ""

echo "7. Test what the JWT_SECRET resolves to:"
echo "docker exec hms-backend sh -c 'echo \$JWT_SECRET'"
echo ""

echo "8. Check if the container can access openssl:"
echo "docker exec hms-backend which openssl"
echo "docker exec hms-backend openssl version"
echo ""

echo "9. Manually test generating a JWT secret inside container:"
echo "docker exec hms-backend openssl rand -base64 48"
echo ""

echo "10. Check container startup logs for environment variable loading:"
echo "docker logs hms-backend | grep -i 'environment\\|property\\|jwt'"
echo ""

echo "Run these commands on your VM to diagnose the issue!"

# docker-compose restart backend

# docker logs -f hms-backend
# docker compose down