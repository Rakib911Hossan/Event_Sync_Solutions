CREATE TABLE IF NOT EXISTS lunch_schedules (
                                                 schedule_id SERIAL PRIMARY KEY,                -- Primary key for the schedule
                                                 user_id INT NOT NULL,                          -- Foreign key referencing "user" table
                                                 menu_item_id INT NOT NULL,                     -- Foreign key referencing "menu_items" table
                                                 scheduled_date VARCHAR(255) NOT NULL,          -- Date for the lunch schedule

    -- Foreign Key Constraints
                                                 CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
                                                 CONSTRAINT fk_menu_item_id FOREIGN KEY (menu_item_id) REFERENCES menu_items (item_id) ON DELETE CASCADE
);
