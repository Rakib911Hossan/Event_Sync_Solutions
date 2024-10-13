CREATE TABLE IF NOT EXISTS menu_items (
                                            item_id SERIAL PRIMARY KEY,
                                            item_name VARCHAR(255) NOT NULL,
                                            description TEXT,
                                            category VARCHAR(50),
                                            available_date DATE
);
