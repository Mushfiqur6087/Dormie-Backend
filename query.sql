-- Admin user
INSERT INTO users (username, email, password_hash, role, created_at) 
VALUES ('admin', 'admin@dormie.com', 'admin123', 'ADMIN', NOW());

-- Hall Manager
INSERT INTO users (username, email, password_hash, role, created_at) 
VALUES ('hallmanager', 'manager@dormie.com', 'manager123', 'HALL_MANAGER', NOW());

-- Authority user
INSERT INTO users (username, email, password_hash, role, created_at) 
VALUES ('authority', 'authority@dormie.com', 'authority123', 'AUTHORITY', NOW());

-- Supervisor user
INSERT INTO users (username, email, password_hash, role, created_at) 
VALUES ('supervisor', 'supervisor@dormie.com', 'supervisor123', 'SUPERVISOR', NOW());

-- Students
INSERT INTO users (username, email, password_hash, role, created_at) 
VALUES ('john_doe', 'john.doe@student.university.edu', 'password123', 'STUDENT', NOW());

INSERT INTO users (username, email, password_hash, role, created_at) 
VALUES ('jane_smith', 'jane.smith@student.university.edu', 'password123', 'STUDENT', NOW());

