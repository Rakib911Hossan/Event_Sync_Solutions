
CREATE TABLE IF NOT EXISTS user (
                                                     id SERIAL PRIMARY KEY,
                                                     username VARCHAR(255) NOT NULL UNIQUE,
                                                     name VARCHAR(255),
                                                     password VARCHAR(255) NOT NULL,
                                                     email VARCHAR(255),
                                                     is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                                     office_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS menu_items (
                                                           item_id SERIAL PRIMARY KEY,
                                                           item_name VARCHAR(255) NOT NULL,
                                                           description TEXT,
                                                           category VARCHAR(50),
                                                           available_date DATE
);

CREATE TABLE IF NOT EXISTS lunch_schedules (
                                                                schedule_id SERIAL PRIMARY KEY,
                                                                user_id INT NOT NULL,
                                                                scheduled_date DATE NOT NULL,
                                                                menu_item_id INT NOT NULL,
                                                                FOREIGN KEY (user_id) REFERENCES user(id),
                                                                FOREIGN KEY (menu_item_id) REFERENCES menu_items(item_id)
);

CREATE TABLE IF NOT EXISTS orders (
                                                       order_id SERIAL PRIMARY KEY,
                                                       user_id INT NOT NULL,
                                                       menu_item_id INT NOT NULL,
                                                       order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                       status VARCHAR(50) NOT NULL,
                                                       FOREIGN KEY (user_id) REFERENCES user(id),
                                                       FOREIGN KEY (menu_item_id) REFERENCES menu_items(item_id)
);

CREATE TABLE IF NOT EXISTS feedback (
                                                         feedback_id SERIAL PRIMARY KEY,
                                                         user_id INT NOT NULL,
                                                         menu_item_id INT NOT NULL,
                                                         rating INT CHECK (rating BETWEEN 1 AND 5),
                                                         comments TEXT,
                                                         FOREIGN KEY (user_id) REFERENCES user(id),
                                                         FOREIGN KEY (menu_item_id) REFERENCES menu_items(item_id)
);

-- Insert fixed roles
INSERT INTO user (username, name, password, email, office_id, is_active)
VALUES
    ('admin', 'Administrator', 'admin_password', 'admin@example.com', 1, TRUE),
    ('user1', 'User One', 'user1_password', 'user1@example.com', 1, TRUE);

-- Insert fixed menu items
INSERT INTO menu_items (item_name, description, category, available_date)
VALUES
    ('Vegetable Salad', 'Fresh vegetable salad', 'lunch', CURRENT_DATE),
    ('Grilled Chicken', 'Grilled chicken with herbs', 'lunch', CURRENT_DATE),
    ('Fruit Platter', 'Assorted seasonal fruits', 'snacks', CURRENT_DATE);
