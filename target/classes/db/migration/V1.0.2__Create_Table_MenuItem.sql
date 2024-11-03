
-- Create MenuItem Table
CREATE TABLE menu_items
(
    id             INT4 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    item_name      VARCHAR(50) NOT NULL, -- Reference to the enum type
    description    VARCHAR(500),
    category       VARCHAR(50)  NOT NULL, -- Reference to the enum type
    available_time VARCHAR(50)         -- Added available_time column

);
-- Inserting dinner items
INSERT INTO menu_items (item_name, description, category, available_time) VALUES
                                                                              ('GRILLED CHICKEN', 'Juicy grilled chicken with herbs', 'LUNCH', '18:00 - 20:00'),
                                                                              ('STEAK', 'Tender steak cooked to perfection', 'DINNER', '18:00 - 20:00'),
                                                                              ('PAELLA', 'Traditional Spanish rice dish with seafood', 'DINNER', '18:00 - 20:00'),
                                                                              ('VEGETABLE STIR-FRY', 'Mixed vegetables stir-fried with soy sauce', 'DINNER', '18:00 - 20:00'),
                                                                              ('FISH TACOS', 'Crispy fish tacos with fresh salsa', 'LUNCH', '18:00 - 20:00'),
                                                                              ('LASAGNA', 'Layers of pasta, meat, and cheese', 'DINNER', '18:00 - 20:00'),
                                                                              ('CHICKEN CURRY', 'Spicy chicken curry served with rice', 'BREAKFAST', '18:00 - 20:00'),
                                                                              ('SPAGHETTI CONGOLESE', 'Classic spaghetti with meat sauce', 'DINNER', '18:00 - 20:00'),
                                                                              ('STUFFED PEPPERS', 'Bell peppers stuffed with rice and meat', 'SNACKS', '18:00 - 20:00'),
                                                                              ('CAESAR SALAD', 'Crisp romaine with Caesar dressing', 'DINNER', '18:00 - 20:00');
