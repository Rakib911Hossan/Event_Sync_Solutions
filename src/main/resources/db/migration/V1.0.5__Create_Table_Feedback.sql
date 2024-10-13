CREATE TABLE IF NOT EXISTS feedback (
                                          feedback_id SERIAL PRIMARY KEY,
                                          user_id INT NOT NULL, -- Foreign key referencing "user" table
                                          menu_item_id INT NOT NULL, -- Foreign key referencing "menu_items" table
                                          rating INT CHECK (rating BETWEEN 1 AND 5),
                                          comments TEXT,
                                          FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE, -- Added ON DELETE CASCADE for consistency
                                          FOREIGN KEY (menu_item_id) REFERENCES menu_items(item_id) ON DELETE CASCADE -- Added ON DELETE CASCADE for consistency
);
