
INSERT INTO categories (name, description) VALUES
('Fiction', 'Novels, short stories, and other fictional works'),
('Non-Fiction', 'Factual works including biographies, histories, and reference books'),
('Science', 'Books related to various scientific disciplines'),
('Computer Science', 'Programming, algorithms, and computer theory books'),
('Mathematics', 'Books on various mathematical topics');


INSERT INTO users (username, email, password, role) VALUES
('john_doe', 'john@example.com', '$2a$10$rPiEAgQNIT1TCoQpyAQ.VOLQpUphlAdUL.iGGQIGYFQKcIzlnZFYq', 'USER'), -- password: password
('jane_smith', 'jane@example.com', '$2a$10$rPiEAgQNIT1TCoQpyAQ.VOLQpUphlAdUL.iGGQIGYFQKcIzlnZFYq', 'USER'),
('bob_jones', 'bob@example.com', '$2a$10$rPiEAgQNIT1TCoQpyAQ.VOLQpUphlAdUL.iGGQIGYFQKcIzlnZFYq', 'USER'),
('alice_walker', 'alice@example.com', '$2a$10$rPiEAgQNIT1TCoQpyAQ.VOLQpUphlAdUL.iGGQIGYFQKcIzlnZFYq', 'USER'),
('sam_wilson', 'sam@example.com', '$2a$10$rPiEAgQNIT1TCoQpyAQ.VOLQpUphlAdUL.iGGQIGYFQKcIzlnZFYq', 'ADMIN');


INSERT INTO books (title, author, isbn, publisher, published_year, total_copies, available_copies, category_id) VALUES
('Database Systems: The Complete Book', 'Hector Garcia-Molina', '978-0131873254', 'Pearson', 2008, 3, 2, 4),
('Introduction to Algorithms', 'Thomas H. Cormen', '978-0262033848', 'MIT Press', 2009, 2, 1, 4),
('To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 'HarperCollins', 1960, 4, 2, 1),
('A Brief History of Time', 'Stephen Hawking', '978-0553380163', 'Bantam', 1988, 2, 1, 3),
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 'Scribner', 1925, 3, 2, 1),
('Clean Code', 'Robert C. Martin', '978-0132350884', 'Prentice Hall', 2008, 2, 1, 4),
('The Immortal Life of Henrietta Lacks', 'Rebecca Skloot', '978-1400052189', 'Crown', 2010, 2, 1, 2),
('Calculus: Early Transcendentals', 'James Stewart', '978-1285741550', 'Cengage Learning', 2015, 3, 2, 5),
('CS 348 Introduction', 'Minh Hieu', '978-1234567890', 'University Press', 2023, 5, 4, 4);


INSERT INTO transactions (book_id, user_id, borrow_date, expected_return_date, actual_return_date, status) VALUES
(1, 1, '2024-02-01', '2024-02-15', '2025-04-27', 'returned'),
(2, 1, '2024-02-10', '2024-02-24', NULL, 'borrowed'),
(3, 2, '2024-01-20', '2024-02-03', '2024-02-05', 'returned'),
(4, 3, '2024-02-15', '2024-03-01', NULL, 'borrowed'),
(5, 4, '2024-02-05', '2024-02-19', '2024-02-18', 'returned'),
(6, 5, '2024-02-12', '2024-02-26', NULL, 'borrowed'),
(7, 2, '2024-01-15', '2024-01-29', '2024-01-25', 'returned'),
(8, 3, '2024-01-10', '2024-01-24', '2024-01-30', 'returned'),
(3, 1, '2025-04-02', '2025-04-17', NULL, 'borrowed'),
(2, 2, '2025-04-12', '2025-04-27', '2025-04-29', 'returned'),
(7, 3, '2025-04-17', '2025-05-01', NULL, 'borrowed'),
(8, 4, '2025-04-02', '2025-04-17', NULL, 'borrowed'),
(6, 1, '2025-04-25', '2025-05-09', NULL, 'borrowed'),
(4, 3, '2025-04-20', '2025-05-04', NULL, 'borrowed'),
(9, 4, '2025-04-30', '2025-05-14', NULL, 'borrowed'),
(8, 5, '2025-04-15', '2025-04-29', NULL, 'borrowed'),
(9, 4, '2025-04-05', '2025-04-19', '2025-04-08', 'returned'),
(8, 5, '2025-03-21', '2025-04-04', '2025-04-04', 'returned'),
(4, 2, '2025-03-06', '2025-03-20', '2025-03-26', 'returned'),
(6, 1, '2025-02-24', '2025-03-10', '2025-03-01', 'returned'),
(5, 3, '2025-04-05', '2025-04-19', NULL, 'borrowed'),
(5, 2, '2025-04-05', '2025-04-20', NULL, 'borrowed'),
(3, 4, '2025-03-21', '2025-04-05', NULL, 'borrowed');