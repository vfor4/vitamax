CREATE TABLE IF NOT EXISTS review (
    id INTEGER PRIMARY KEY,
    course_id INTEGER NOT NULL,
    author VARCHAR(255),
    subject VARCHAR(255),
content TEXT );

INSERT INTO review (id, course_id, author, subject, content) VALUES
 (1, 101, 'Alice Nguyen', 'Great course', 'The course was very well structured and easy to follow.'),
 (2, 101, 'Bob Tran', 'Helpful content', 'I learned many practical concepts that I can apply at work.'),
 (3, 102, 'Charlie Pham', 'Too fast', 'Some parts were explained too quickly for beginners.'),
 (4, 103, 'Daisy Le', 'Excellent instructor', 'The instructor explained complex topics very clearly.'),
 (5, 102, 'Eric Vo', 'Good but could improve', 'Overall good, but more real-world examples would help.'),
 (6, 104, 'Fiona Hoang', 'Highly recommended', 'Perfect course for anyone starting in this field.');
