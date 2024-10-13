CREATE TABLE IF NOT EXISTS orders (
                                      order_id SERIAL PRIMARY KEY,
                                      user_id INT NOT NULL, -- Foreign key referencing user table
                                      menu_item_id INT NOT NULL, -- Foreign key referencing menu_items table
                                      order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      status VARCHAR(50) NOT NULL,
                                      FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE, -- Foreign key reference
                                      FOREIGN KEY (menu_item_id) REFERENCES menu_items(item_id) ON DELETE CASCADE -- Foreign key reference
);
