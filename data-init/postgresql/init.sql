CREATE TABLE IF NOT EXISTS review (
    id VARCHAR(36) PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL,
    author VARCHAR(255),
    subject VARCHAR(255),
    content TEXT,
    version int4);

INSERT INTO review (id, course_id, author, subject, content) VALUES
 ('aab16e17-63c3-4248-9731-248b58592b60', 'fab16e17-63c3-4248-9731-248b58592b61', 'Alice Nguyen', 'Great course', 'The course was very well structured and easy to follow.'),
 ('aab16e17-63c3-4248-9731-248b58592b61', 'fab16e17-63c3-4248-9731-248b58592b61', 'Bob Tran', 'Helpful content', 'I learned many practical concepts that I can apply at work.'),
 ('aab16e17-63c3-4248-9731-248b58592b62', 'fab16e17-63c3-4248-9731-248b58592b62', 'Charlie Pham', 'Too fast', 'Some parts were explained too quickly for beginners.'),
 ('aab16e17-63c3-4248-9731-248b58592b63', 'fab16e17-63c3-4248-9731-248b58592b63', 'Daisy Le', 'Excellent instructor', 'The instructor explained complex topics very clearly.'),
 ('aab16e17-63c3-4248-9731-248b58592b64', 'fab16e17-63c3-4248-9731-248b58592b62', 'Eric Vo', 'Good but could improve', 'Overall good, but more real-world examples would help.'),
 ('aab16e17-63c3-4248-9731-248b58592b65', 'fab16e17-63c3-4248-9731-248b58592b64', 'Fiona Hoang', 'Highly recommended', 'Perfect course for anyone starting in this field.');
