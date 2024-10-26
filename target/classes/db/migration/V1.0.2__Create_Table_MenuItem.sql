-- Create ItemName Enum Type
CREATE TYPE item_name AS ENUM (
    'SANDWICH',
    'PASTA',
    'BURGER',
    'SALAD',
    'PIZZA',
    'SOUP',
    'FRIES',
    'GRILLED CHICKEN',
    'STEAK',
    'PAELLA',
    'VEGETABLE STIR-FRY',
    'FISH TACOS',
    'LASAGNA',
    'CHICKEN CURRY',
    'SPAGHETTI CONGOLESE',
    'STUFFED PEPPERS',
    'CAESAR SALAD'
    );


-- Create Category Enum Type
CREATE TYPE category AS ENUM (
    'BREAKFAST',
    'LUNCH',
    'SNACKS',
    'DINNER'  -- Added missing comma
    );

-- Create MenuItem Table
CREATE TABLE menu_items (
                            id INT4 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                            item_name item_name NOT NULL,  -- Reference to the enum type
                            description TEXT,
                            category category NOT NULL,      -- Reference to the enum type
                            available_time VARCHAR(50),      -- Added available_time column
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserting dinner items
INSERT INTO menu_items (item_name, description, category, available_time) VALUES
                                                                              ('GRILLED CHICKEN', 'Juicy grilled chicken with herbs', 'DINNER', '18:00 - 20:00'),
                                                                              ('STEAK', 'Tender steak cooked to perfection', 'DINNER', '18:00 - 20:00'),
                                                                              ('PAELLA', 'Traditional Spanish rice dish with seafood', 'DINNER', '18:00 - 20:00'),
                                                                              ('VEGETABLE STIR-FRY', 'Mixed vegetables stir-fried with soy sauce', 'DINNER', '18:00 - 20:00'),
                                                                              ('FISH TACOS', 'Crispy fish tacos with fresh salsa', 'DINNER', '18:00 - 20:00'),
                                                                              ('LASAGNA', 'Layers of pasta, meat, and cheese', 'DINNER', '18:00 - 20:00'),
                                                                              ('CHICKEN CURRY', 'Spicy chicken curry served with rice', 'DINNER', '18:00 - 20:00'),
                                                                              ('SPAGHETTI CONGOLESE', 'Classic spaghetti with meat sauce', 'DINNER', '18:00 - 20:00'),
                                                                              ('STUFFED PEPPERS', 'Bell peppers stuffed with rice and meat', 'DINNER', '18:00 - 20:00'),
                                                                              ('CAESAR SALAD', 'Crisp romaine with Caesar dressing', 'DINNER', '18:00 - 20:00');
