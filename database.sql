CREATE DATABASE IF NOT EXISTS wk10_lab;
USE wk10_lab;

CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(40),
    email VARCHAR(150),
    education VARCHAR(150),
    gender VARCHAR(20),
    available_date DATE NOT NULL,
    position VARCHAR(150),
    salary DECIMAL(12,2),
    authorized BOOLEAN,
    relatives BOOLEAN,
    explanation TEXT,
    signature VARCHAR(150)
);

-- After submitting the form, run this to show the saved row:
SELECT * FROM applications;
