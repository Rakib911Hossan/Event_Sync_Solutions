CREATE TABLE IF NOT EXISTS "user" (
                                      id SERIAL PRIMARY KEY,
                                      username VARCHAR(255) NOT NULL UNIQUE, -- Added username
                                      name VARCHAR(255),
                                      password VARCHAR(255) NOT NULL, -- Consider hashing this
                                      email VARCHAR(255),
                                      is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                      office_id INT NOT NULL
);
