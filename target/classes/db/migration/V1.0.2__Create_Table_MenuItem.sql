
-- Create MenuItem Table
CREATE TABLE menu_items
(
    id             INT4 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    item_name      VARCHAR(50) NOT NULL, -- Reference to the enum type
    description    VARCHAR(500),
    category       VARCHAR(50)  NOT NULL, -- Reference to the enum type
    available_time VARCHAR(50) ,
    price  INT4,
    item_pic VARCHAR(255)-- Added available_time column

);

