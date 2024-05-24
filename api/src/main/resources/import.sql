-- member (password : Test!test1)
INSERT INTO member (member_id, email, name, password, role, tel, city, street, zipcode, login_fail_count, is_locked, created_date_time, last_modified_date_time) VALUES (1, 'user001@gmail.com', 'user001', '$2a$10$Z2ucukPIW3nazwfPnc2ed.q.cF494nwAbzX3z6hTua7R2HJLsZIca', 'USER', '01011111001', '테스트', '테스트 123-123', '11111', 0, b'0', NOW(), NOW());
INSERT INTO member (member_id, email, name, password, role, tel, city, street, zipcode, login_fail_count, is_locked, created_date_time, last_modified_date_time) VALUES (2, 'user002@gmail.com', 'user002', '$2a$10$Z2ucukPIW3nazwfPnc2ed.q.cF494nwAbzX3z6hTua7R2HJLsZIca', 'USER', '01011111002', '테스트', '테스트 123-123', '11111', 0, b'0', NOW(), NOW());
INSERT INTO member (member_id, email, name, password, role, tel, city, street, zipcode, login_fail_count, is_locked, created_date_time, last_modified_date_time) VALUES (3, 'user003@gmail.com', 'user003', '$2a$10$Z2ucukPIW3nazwfPnc2ed.q.cF494nwAbzX3z6hTua7R2HJLsZIca', 'USER', '01011111003', '테스트', '테스트 123-123', '11111', 0, b'0', NOW(), NOW());
INSERT INTO member (member_id, email, name, password, role, tel, city, street, zipcode, login_fail_count, is_locked, created_date_time, last_modified_date_time) VALUES (4, 'admin001@gmail.com', 'admin001', '$2a$10$Z2ucukPIW3nazwfPnc2ed.q.cF494nwAbzX3z6hTua7R2HJLsZIca', 'ADMIN', '01011111004', '테스트', '테스트 123-123', '11111', 0, b'0', NOW(), NOW());

-- category
INSERT INTO category (category_id, name, parent_id, created_date_time, last_modified_date_time) VALUES (1, 'main1', NULL, NOW(), NOW());
INSERT INTO category (category_id, name, parent_id, created_date_time, last_modified_date_time) VALUES (2, 'main2', NULL, NOW(), NOW());
INSERT INTO category (category_id, name, parent_id, created_date_time, last_modified_date_time) VALUES (3, 'sub1-1', 1, NOW(), NOW());
INSERT INTO category (category_id, name, parent_id, created_date_time, last_modified_date_time) VALUES (4, 'sub1-2', 1, NOW(), NOW());
INSERT INTO category (category_id, name, parent_id, created_date_time, last_modified_date_time) VALUES (5, 'sub2-1', 2, NOW(), NOW());
INSERT INTO category (category_id, name, parent_id, created_date_time, last_modified_date_time) VALUES (6, 'sub2-2', 2, NOW(), NOW());

-- product
INSERT INTO product (product_id, name, details, comp_id, active_for_sale, created_date_time, last_modified_date_time) VALUES (1, 'product1', 'details1', 5, b'0', NOW(), NOW());
INSERT INTO product (product_id, name, details, comp_id, active_for_sale, created_date_time, last_modified_date_time) VALUES (2, 'product2', 'details2', 5, b'0', NOW(), NOW());

-- product_category
INSERT INTO product_category (product_id, category_id) VALUES (1, 3);
INSERT INTO product_category (product_id, category_id) VALUES (1, 5);
INSERT INTO product_category (product_id, category_id) VALUES (2, 4);

-- options
INSERT INTO options (option_id, product_id, name, price) VALUES (1, 1, 'product1-option1', 0);
INSERT INTO options (option_id, product_id, name, price) VALUES (2, 1, 'product1-option2', 10);
INSERT INTO options (option_id, product_id, name, price) VALUES (3, 2, 'product2-option1', 0);
INSERT INTO options (option_id, product_id, name, price) VALUES (4, 2, 'product2-option2', 100);