-- Create the databases
CREATE DATABASE IF NOT EXISTS productcatalogusservice_db;
CREATE DATABASE IF NOT EXISTS winkelwagenservice_db;

-- Create the user if it doesn't already exist (optional, but useful if not set up elsewhere)
CREATE USER IF NOT EXISTS 'user'@'%' IDENTIFIED BY 'password';

-- Grant all privileges on the databases to the specified user
GRANT ALL PRIVILEGES ON productcatalogusservice_db.* TO 'user'@'%';
GRANT ALL PRIVILEGES ON winkelwagenservice_db.* TO 'user'@'%';

-- Apply the changes
FLUSH PRIVILEGES;
