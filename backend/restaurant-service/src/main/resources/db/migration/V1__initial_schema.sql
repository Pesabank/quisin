-- Create restaurant table
CREATE TABLE restaurants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create location table
CREATE TABLE locations (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL REFERENCES restaurants(id),
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create operating_hours table
CREATE TABLE operating_hours (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL REFERENCES restaurants(id),
    day_of_week VARCHAR(20) NOT NULL,
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create chains table
CREATE TABLE chains (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create restaurant_chain table
CREATE TABLE restaurant_chain (
    restaurant_id UUID NOT NULL REFERENCES restaurants(id),
    chain_id UUID NOT NULL REFERENCES chains(id),
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (restaurant_id, chain_id)
);

-- Create indexes
CREATE INDEX idx_restaurants_owner_id ON restaurants(owner_id);
CREATE INDEX idx_restaurants_status ON restaurants(status);
CREATE INDEX idx_chains_owner_id ON chains(owner_id);
CREATE INDEX idx_locations_city ON locations(city);
CREATE INDEX idx_locations_country ON locations(country); 