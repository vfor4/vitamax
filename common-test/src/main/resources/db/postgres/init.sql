CREATE TABLE IF NOT EXISTS review (
    id VARCHAR(36) PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL,
    author VARCHAR(255),
    subject VARCHAR(255),
    content TEXT );