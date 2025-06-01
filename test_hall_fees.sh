#!/bin/bash

# Hall Fee Testing Script for Dormie Backend
# This script tests the complete hall fee workflow following the proper sequence:
# 1. Admin creates users (automatically creates students with default values)
# 2. Students login and update their information (like residency status)
# 3. Admin (reusing saved token) creates hall fees and checks student hall fees

BASE_URL="http://localhost:8080"
CONTENT_TYPE="Content-Type: application/json"

echo "🚀 Starting Hall Fee Testing Workflow"
echo "=================================="

# Step 1: Admin Login and Save Token
echo "📝 Step 1: Admin Login"
ADMIN_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/signin" \
  -H "${CONTENT_TYPE}" \
  -d '{
    "email": "admin@dormie.com",
    "password": "Admin123!"
  }')

# Extract token using accessToken field name
ADMIN_TOKEN=$(echo "$ADMIN_LOGIN_RESPONSE" | jq -r '.accessToken')

if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" = "null" ]; then
  echo "❌ Admin login failed!"
  echo "Response: $ADMIN_LOGIN_RESPONSE"
  exit 1
fi

echo "✅ Admin login successful"
echo "Admin Token: $ADMIN_TOKEN"
echo ""

# Step 2: Admin Creates First Student (will automatically get default values)
echo "📝 Step 2: Admin Creating First Student (will change to resident later)"
STUDENT1_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/admin/signup" \
  -H "${CONTENT_TYPE}" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "username": "John Doe",
    "email": "john.doe@student.edu",
    "password": "Student123!",
    "role": "STUDENT",
    "studentId": 101
  }')

echo "Student 1 Creation Response:"
echo "$STUDENT1_RESPONSE" | jq '.'
echo ""

# Step 3: Admin Creates Second Student (will remain attached)
echo "📝 Step 3: Admin Creating Second Student (will remain attached)"
STUDENT2_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/admin/signup" \
  -H "${CONTENT_TYPE}" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "username": "Jane Smith",
    "email": "jane.smith@student.edu",
    "password": "Student123!",
    "role": "STUDENT",
    "studentId": 102
  }')

echo "Student 2 Creation Response:"
echo "$STUDENT2_RESPONSE" | jq '.'
echo ""

# Step 4: First Student Logs In
echo "📝 Step 4: First Student Login"
STUDENT1_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/signin" \
  -H "${CONTENT_TYPE}" \
  -d '{
    "email": "john.doe@student.edu",
    "password": "Student123!"
  }')

STUDENT1_TOKEN=$(echo "$STUDENT1_LOGIN_RESPONSE" | jq -r '.accessToken')

if [ -z "$STUDENT1_TOKEN" ] || [ "$STUDENT1_TOKEN" = "null" ]; then
  echo "❌ Student 1 login failed!"
  echo "Response: $STUDENT1_LOGIN_RESPONSE"
  exit 1
fi

echo "✅ Student 1 login successful"
echo ""

# Step 5: First Student Updates Their Information (changing residency to resident)
echo "📝 Step 5: First Student Updating Information (changing to resident)"
UPDATE1_RESPONSE=$(curl -s -X PUT "${BASE_URL}/api/students/update" \
  -H "${CONTENT_TYPE}" \
  -H "Authorization: Bearer $STUDENT1_TOKEN" \
  -d '{
    "department": "Computer Science",
    "batch": 2024,
    "contactNo": "01234567890",
    "presentAddress": "123 Student Dormitory",
    "permanentAddress": "456 Home Town",
    "residencyStatus": "resident"
  }')

echo "Student 1 Update Response:"
echo "$UPDATE1_RESPONSE" | jq '.'
echo ""

# Step 6: Second Student Logs In
echo "📝 Step 6: Second Student Login"
STUDENT2_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/signin" \
  -H "${CONTENT_TYPE}" \
  -d '{
    "email": "jane.smith@student.edu",
    "password": "Student123!"
  }')

STUDENT2_TOKEN=$(echo "$STUDENT2_LOGIN_RESPONSE" | jq -r '.accessToken')

if [ -z "$STUDENT2_TOKEN" ] || [ "$STUDENT2_TOKEN" = "null" ]; then
  echo "❌ Student 2 login failed!"
  echo "Response: $STUDENT2_LOGIN_RESPONSE"
  exit 1
fi

echo "✅ Student 2 login successful"
echo ""

# Step 7: Second Student Updates Their Information (keeping as attached)
echo "📝 Step 7: Second Student Updating Information (remaining attached)"
UPDATE2_RESPONSE=$(curl -s -X PUT "${BASE_URL}/api/students/update" \
  -H "${CONTENT_TYPE}" \
  -H "Authorization: Bearer $STUDENT2_TOKEN" \
  -d '{
    "department": "Mathematics",
    "batch": 2024,
    "contactNo": "01234567891",
    "presentAddress": "789 Off-Campus Housing",
    "permanentAddress": "101 Family Village",
    "residencyStatus": "attached"
  }')

echo "Student 2 Update Response:"
echo "$UPDATE2_RESPONSE" | jq '.'
echo ""

echo "🎉 Student Setup Phase Completed!"
echo "================================"
echo ""

# Step 8: Admin Creates Hall Fee for Attached Students (using saved token)
echo "📝 Step 8: Admin Creating Hall Fee for Attached Students"
ATTACHED_FEE_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/hall-fees" \
  -H "${CONTENT_TYPE}" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "type": "attached",
    "fee": 3000.00,
    "year": 2024
  }')

echo "Attached Hall Fee Response:"
echo "$ATTACHED_FEE_RESPONSE" | jq '.'
echo ""

# Step 9: Admin Creates Hall Fee for Resident Students (using saved token)
echo "📝 Step 9: Admin Creating Hall Fee for Resident Students"
RESIDENT_FEE_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/hall-fees" \
  -H "${CONTENT_TYPE}" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "type": "resident",
    "fee": 5000.00,
    "year": 2024
  }')

echo "Resident Hall Fee Response:"
echo "$RESIDENT_FEE_RESPONSE" | jq '.'
echo ""

# Step 10: Admin Gets All Student Hall Fees (should be automatically created)
echo "📝 Step 10: Admin Getting All Student Hall Fees (should be auto-created)"
ALL_STUDENT_HALL_FEES_RESPONSE=$(curl -s -X GET "${BASE_URL}/api/student-hall-fees" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

echo "All Student Hall Fees: $ALL_STUDENT_HALL_FEES_RESPONSE"
echo ""

echo "🎉 Hall Fee Testing Workflow Completed!"
echo "======================================="
echo ""
echo "📋 Summary of Actions:"
echo "1. ✅ Admin logged in and token saved"
echo "2. ✅ Admin created 2 students (with default values: attached, batch 2025, etc.)"
echo "3. ✅ Student 1 (John Doe) logged in and updated info (changed to resident)"
echo "4. ✅ Student 2 (Jane Smith) logged in and updated info (remained attached)"
echo "5. ✅ Admin created hall fee for attached students: 3000.00"
echo "6. ✅ Admin created hall fee for resident students: 5000.00"
echo "7. ✅ Admin retrieved all student hall fees"
echo ""
echo "📊 Expected Results:"
echo "- John Doe should have a hall fee of 5000.00 (resident type)"
echo "- Jane Smith should have a hall fee of 3000.00 (attached type)"
echo "- Both should have status 'UNPAID' by default"
echo "- Student hall fees should be automatically created when hall fees are added"
echo ""
