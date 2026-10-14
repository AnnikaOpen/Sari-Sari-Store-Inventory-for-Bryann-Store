-- Extra table for the app's login screen (add this to the team SQL file).
-- The app also creates it automatically if it is missing.
USE sari_sari_store;

CREATE TABLE IF NOT EXISTS LOGIN_ACCOUNT (
    Username VARCHAR(50) PRIMARY KEY,
    Password VARCHAR(64) NOT NULL
);

INSERT INTO LOGIN_ACCOUNT (Username, Password)
VALUES ('admin', SHA2('admin123', 256));
