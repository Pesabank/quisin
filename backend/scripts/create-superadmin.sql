-- Drop existing tables and types if they exist
DROP TABLE IF EXISTS "User" CASCADE;
DROP TYPE IF EXISTS "Role" CASCADE;

-- Create Role enum
CREATE TYPE "Role" AS ENUM ('USER', 'ADMIN', 'SUPER_ADMIN');

-- Create User table
CREATE TABLE "User" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role "Role" NOT NULL DEFAULT 'USER',
    "restaurantId" UUID,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert superadmin
INSERT INTO "User" (id, email, password, name, role)
VALUES (
    '00000000-0000-0000-0000-000000000000',
    'superadmin@quisin.com',
    'pbkdf2_sha256$260000$8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92$Telemux01!!',
    'Super Admin',
    'SUPER_ADMIN'
); 