-- CreateEnum
CREATE TYPE "Role" AS ENUM ('USER', 'ADMIN', 'SUPER_ADMIN');

-- Add Superadmin User
INSERT INTO "User" ("id", "email", "password", "name", "role", "createdAt", "updatedAt")
VALUES (
  '00000000-0000-0000-0000-000000000000',
  'superadmin@quisin.com',
  'pbkdf2_sha256$260000$8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92$Telemux01!!',
  'Super Admin',
  'SUPER_ADMIN',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
); 