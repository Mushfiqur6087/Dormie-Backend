#!/bin/bash

echo "📋 Docker Container Logs Inspection Commands"
echo "============================================"

echo "1. View all logs from the backend container:"
echo "docker logs hms-backend"
echo ""

echo "2. View last 50 lines of logs:"
echo "docker logs --tail 50 hms-backend"
echo ""

echo "3. Follow logs in real-time:"
echo "docker logs -f hms-backend"
echo ""

echo "4. Search for JWT-related errors:"
echo "docker logs hms-backend | grep -i jwt"
echo ""

echo "5. Search for authentication errors:"
echo "docker logs hms-backend | grep -i 'auth\\|signin\\|login'"
echo ""

echo "6. Search for environment variable issues:"
echo "docker logs hms-backend | grep -i 'environment\\|property'"
echo ""

echo "7. Search for base64 or decoding errors:"
echo "docker logs hms-backend | grep -i 'base64\\|decoding\\|illegal'"
echo ""

echo "8. Search for startup completion:"
echo "docker logs hms-backend | grep -i 'started\\|ready'"
echo ""

echo "9. Search for database connection logs:"
echo "docker logs hms-backend | grep -i 'database\\|postgres\\|connection'"
echo ""

echo "10. Search for admin user creation:"
echo "docker logs hms-backend | grep -i 'admin\\|bootstrap'"
echo ""

echo "11. View logs with timestamps:"
echo "docker logs -t hms-backend"
echo ""

echo "12. View logs since a specific time (last 10 minutes):"
echo "docker logs --since 10m hms-backend"
echo ""

echo "📌 Run these commands on your VM to see the logs!"
