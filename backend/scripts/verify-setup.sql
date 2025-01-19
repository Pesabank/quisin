\c quisin_user_db;

-- Check if Role type exists
SELECT typname, typcategory FROM pg_type WHERE typname = 'role';

-- Check if User table exists
\dt "User";

-- Check if superadmin exists
SELECT email, role, name FROM "User" WHERE role = 'SUPER_ADMIN'; 