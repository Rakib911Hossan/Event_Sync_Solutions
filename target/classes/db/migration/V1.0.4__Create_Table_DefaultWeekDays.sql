-- Create the table for DefaultWeekDays entity
CREATE TABLE IF NOT EXISTS default_Weekdays
(
    id           INT4 PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- Assuming GenericEntity<Integer> uses 'id' as primary key
    user_id      Int4 NOT NULL,                                 -- Foreign key referencing the User table
    days         VARCHAR(255),                                  -- String column to store days as comma-separated values
    is_week_days BOOLEAN DEFAULT TRUE                        -- Boolean column for isWeakDays
);

ALTER TABLE default_Weekdays
    ADD CONSTRAINT default_Weekdays_fk_user
        FOREIGN KEY (user_id)
            REFERENCES "user" (id) on delete cascade ;