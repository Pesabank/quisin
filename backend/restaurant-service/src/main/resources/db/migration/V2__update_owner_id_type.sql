-- Update owner_id column type in restaurants table
ALTER TABLE restaurants
    ALTER COLUMN owner_id TYPE UUID USING owner_id::uuid;

-- Update owner_id column type in chains table
ALTER TABLE chains
    ALTER COLUMN owner_id TYPE UUID USING owner_id::uuid; 