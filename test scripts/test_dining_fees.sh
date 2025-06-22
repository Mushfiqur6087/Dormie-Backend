BASE_URL="http://172.187.160.142:8080"
CONTENT_TYPE="Content-Type: application/json"

echo "🚀 Starting Dining Fee Testing Workflow"
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

# Step 8: Admin Creates First Dining Fee (for 2024)
echo "📝 Step 8: Admin Creating First Dining Fee (2024 - Resident Only)"
DINING_FEE1_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/dining-fees" \
  -H "${CONTENT_TYPE}" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "type": "resident",
    "year": 2024,
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "fee": 8000.00
  }')

echo "Dining Fee 1 (2024) Creation Response:"
echo "$DINING_FEE1_RESPONSE" | jq '.'
echo ""

# Step 9: Admin Creates Second Dining Fee (for 2025)
echo "📝 Step 9: Admin Creating Second Dining Fee (2025 - Resident Only)"
DINING_FEE2_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/dining-fees" \
  -H "${CONTENT_TYPE}" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "type": "resident",
    "year": 2025,
    "startDate": "2025-01-01",
    "endDate": "2025-12-31",
    "fee": 8500.00
  }')

echo "Dining Fee 2 (2025) Creation Response:"
echo "$DINING_FEE2_RESPONSE" | jq '.'
echo ""

# Step 10: Verify Only Resident Student Gets Dining Fees
echo "📝 Step 10: Checking Student Dining Fee Assignments"

# Get user IDs by finding the users by their email addresses
echo "📍 Finding User IDs for the created students..."

# Get Student 1 User ID
STUDENT1_USER_ID=$(curl -s -X GET "${BASE_URL}/api/students" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | \
  jq -r '.[] | select(.userId != null and (.firstName + " " + .lastName | test("John Doe"))) | .userId' | head -1)

# Get Student 2 User ID  
STUDENT2_USER_ID=$(curl -s -X GET "${BASE_URL}/api/students" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | \
  jq -r '.[] | select(.userId != null and (.firstName + " " + .lastName | test("Jane Smith"))) | .userId' | head -1)

echo "Student 1 (Resident) User ID: $STUDENT1_USER_ID"
echo "Student 2 (Attached) User ID: $STUDENT2_USER_ID"
echo ""

# Check dining fees for Student 1 (resident)
echo "📋 Checking Dining Fees for Student 1 (Resident):"
STUDENT1_DINING_FEES=$(curl -s -X GET "${BASE_URL}/api/student-dining-fees/user/${STUDENT1_USER_ID}" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

echo "Student 1 Dining Fees:"
echo "$STUDENT1_DINING_FEES" | jq '.'
echo ""

# Check dining fees for Student 2 (attached)
echo "📋 Checking Dining Fees for Student 2 (Attached):"
STUDENT2_DINING_FEES=$(curl -s -X GET "${BASE_URL}/api/student-dining-fees/user/${STUDENT2_USER_ID}" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

echo "Student 2 Dining Fees:"
echo "$STUDENT2_DINING_FEES" | jq '.'
echo ""

# Step 11: Get All Dining Fees
echo "📝 Step 11: Retrieving All Dining Fees"
ALL_DINING_FEES=$(curl -s -X GET "${BASE_URL}/api/dining-fees" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

echo "All Dining Fees:"
echo "$ALL_DINING_FEES" | jq '.'
echo ""

echo "✅ Dining Fee Testing Completed!"
echo "================================"
echo "Summary:"
echo "- Created 2 dining fees (2024: ৳8000, 2025: ৳8500)"
echo "- Dining fees are only for RESIDENT students"
echo "- Student 1 (Resident) should have dining fee assignments"
echo "- Student 2 (Attached) should NOT have dining fee assignments"
echo ""