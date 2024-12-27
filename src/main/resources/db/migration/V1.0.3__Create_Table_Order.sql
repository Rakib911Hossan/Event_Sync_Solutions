
-- Create 'orders' table
CREATE TABLE IF NOT EXISTS orders (
                                      id INT4 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                      user_id Int4 NOT NULL, -- Foreign key referencing user table
                                      menu_item_id Int4 NOT NULL, -- Foreign key referencing "menu_items" table
                                      order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                      status VARCHAR(50) NOT NULL CHECK (status IN ('ORDERED','PREPARED','SERVED'))
);

ALTER TABLE orders
    ADD CONSTRAINT orders_fk_user
        FOREIGN KEY (user_id)
            REFERENCES "user" (id) on delete cascade ;
ALTER TABLE orders
    ADD CONSTRAINT orders_fk_menu_items
        FOREIGN KEY (menu_item_id)
            REFERENCES menu_items (id) on delete cascade ;

-- CREATE TABLE IF NOT EXISTS orders (

--                                       order_id BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1) PRIMARY KEY,
--                                       user_id BIGINT NOT NULL, -- Foreign key referencing "user" table
--                                       order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--                                       status VARCHAR(50) NOT NULL CHECK (status IN ('ORDERED','PREPARED','SERVED')),
--                                       FOREIGN KEY (user_id) REFERENCES "user" (user_id) ON DELETE CASCADE -- Foreign key reference to user table
-- );

-- -- Create 'orders' table
-- CREATE TABLE IF NOT EXISTS orders (
--                                       order_id bigint PRIMARY KEY,
--                                       user_id INT NOT NULL, -- Foreign key referencing user table
-- --                                       menu_item_id INT NOT NULL, -- Foreign key referencing "menu_items" table
--                                       order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--                                       status VARCHAR(50) NOT NULL CHECK (status IN ('ORDERED','PREPARED','SERVED')),
--                                       FOREIGN KEY (user_id) REFERENCES "user" (user_id) ON DELETE CASCADE -- Foreign key reference
-- --                                       FOREIGN KEY (menu_item_id) REFERENCES menu_items (item_id) ON DELETE CASCADE -- Foreign key reference
-- );

-- CREATE SEQUENCE IF NOT EXISTS order_id_ AS INTEGER START 1 INCREMENT 1;
--
-- -- Create 'orders' table
-- CREATE TABLE IF NOT EXISTS orders (
--                                       order_id INT4 DEFAULT nextval('order_id_') PRIMARY KEY,
--                                       user_id INT NOT NULL,  -- Foreign key referencing user table
--     -- menu_item_id INT NOT NULL,  -- Foreign key referencing "menu_items" table
--                                       order_date TIMESTAMP,  -- Allowing NULL for order_date
--                                       status VARCHAR(50) NOT NULL CHECK (status IN ('ORDERED', 'PREPARED', 'SERVED')),
--                                       FOREIGN KEY (user_id) REFERENCES "user" (uid) ON DELETE CASCADE  -- Foreign key reference
--     -- FOREIGN KEY (menu_item_id) REFERENCES menu_items (item_id) ON DELETE CASCADE  -- Foreign key reference
-- );
