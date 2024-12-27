--
-- -- Create 'user' table
-- CREATE TABLE IF NOT EXISTS "user" (
--                                       id BIGINT PRIMARY KEY,
--                                       name VARCHAR(255) NOT NULL,
--                                       email VARCHAR(255) NOT NULL UNIQUE,
--                                       department VARCHAR(255),
--                                       role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'USER')), -- Enum values
--                                       password VARCHAR(255) NOT NULL, -- Consider hashing this
--                                       is_active BOOLEAN DEFAULT TRUE,
--                                       office_id INT -- Foreign key reference might be needed here
-- );

-- Create sequence for 'user_id'
-- Create 'user' table
CREATE TABLE IF NOT EXISTS "user" (
                                      id INT4 PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- Use IDENTITY for auto-incrementing
                                      name VARCHAR(255) NOT NULL,
                                      phone VARCHAR(255) NOT NULL,
                                      email VARCHAR(255) NOT NULL UNIQUE,
                                      address VARCHAR(255) NOT NULL,
                                      department VARCHAR(255),
                                      role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'USER')), -- Enum for roles
                                      password VARCHAR(255) NOT NULL, -- Consider hashing this
                                      is_active BOOLEAN DEFAULT TRUE,
                                      office_id INT4, -- Foreign key reference might be added here later
                                        user_pic VARCHAR(255)
);



-- -- Create 'defaultWeekendEntity' table in 'public' schema
-- CREATE TABLE IF NOT EXISTS default_Weekend_Entity (
--                                                            id SERIAL PRIMARY KEY,
--                                                            user_id INT NOT NULL,
--                                                            friday BOOLEAN DEFAULT TRUE,
--                                                            saturday BOOLEAN DEFAULT TRUE,
--                                                            sunday BOOLEAN DEFAULT TRUE,
--                                                            monday BOOLEAN DEFAULT TRUE,
--                                                            tuesday BOOLEAN DEFAULT TRUE,
--                                                            wednesday BOOLEAN DEFAULT TRUE,
--                                                            thursday BOOLEAN DEFAULT TRUE,
--                                                            created_at TIMESTAMP ,
--                                                            updated_at TIMESTAMP ,
--                                                            created_by VARCHAR(255),
--                                                            updated_by VARCHAR(255),
--                                                            is_active BOOLEAN NOT NULL DEFAULT FALSE,
--                                                            CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
-- );
