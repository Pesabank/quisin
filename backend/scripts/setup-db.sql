-- Create database
CREATE DATABASE quisin_user_db;

-- Connect to the database
\c quisin_user_db;

-- Create Role enum type
DROP TYPE IF EXISTS Role CASCADE;
CREATE TYPE Role AS ENUM ('SUPER_ADMIN', 'ADMIN', 'KITCHEN_STAFF', 'WAITER', 'CUSTOMER');

-- Drop existing tables if they exist
DROP TABLE IF EXISTS users CASCADE;

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role Role NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    restaurant_id UUID UNIQUE,
    enabled BOOLEAN DEFAULT true,
    locked BOOLEAN DEFAULT false,
    created_by VARCHAR(255),
    created_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_at TIMESTAMP
);

-- Insert superadmin user
INSERT INTO users (
    email,
    password,
    role,
    first_name,
    last_name,
    created_by,
    created_at
) VALUES (
    'superadmin@quisin.com',
    '$2a$10$YF5.rUZ9eVWXJOmEOcKPZOxQk2h.t3/RYEhvITOMbaqY/zdlw0Zx.',  -- BCrypt hash for 'Admin123!'
    'SUPER_ADMIN',
    'Super',
    'Admin',
    'SYSTEM',
    CURRENT_TIMESTAMP
);

-- Insert admin user
INSERT INTO users (
    email,
    password,
    role,
    first_name,
    last_name,
    created_by,
    created_at
) VALUES (
    'admin@quisin.com',
    '$2a$10$YF5.rUZ9eVWXJOmEOcKPZOxQk2h.t3/RYEhvITOMbaqY/zdlw0Zx.',  -- BCrypt hash for 'Admin123!'
    'ADMIN',
    'Admin',
    'User',
    'SYSTEM',
    CURRENT_TIMESTAMP
);

-- Insert kitchen staff user
INSERT INTO users (
    email,
    password,
    role,
    first_name,
    last_name,
    created_by,
    created_at
) VALUES (
    'kitchen@quisin.com',
    '$2a$10$YF5.rUZ9eVWXJOmEOcKPZOxQk2h.t3/RYEhvITOMbaqY/zdlw0Zx.',  -- BCrypt hash for 'Admin123!'
    'KITCHEN_STAFF',
    'Kitchen',
    'Staff',
    'SYSTEM',
    CURRENT_TIMESTAMP
);

-- Insert waiter user
INSERT INTO users (
    email,
    password,
    role,
    first_name,
    last_name,
    created_by,
    created_at
) VALUES (
    'waiter@quisin.com',
    '$2a$10$YF5.rUZ9eVWXJOmEOcKPZOxQk2h.t3/RYEhvITOMbaqY/zdlw0Zx.',  -- BCrypt hash for 'Admin123!'
    'WAITER',
    'Waiter',
    'Staff',
    'SYSTEM',
    CURRENT_TIMESTAMP
); 