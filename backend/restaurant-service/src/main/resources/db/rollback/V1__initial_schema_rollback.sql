-- Drop indexes
DROP INDEX IF EXISTS idx_locations_country;
DROP INDEX IF EXISTS idx_locations_city;
DROP INDEX IF EXISTS idx_chains_owner_id;
DROP INDEX IF EXISTS idx_restaurants_status;
DROP INDEX IF EXISTS idx_restaurants_owner_id;

-- Drop tables in correct order
DROP TABLE IF EXISTS restaurant_chain;
DROP TABLE IF EXISTS chains;
DROP TABLE IF EXISTS operating_hours;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS restaurants; 