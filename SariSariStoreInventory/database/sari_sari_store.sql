-- =========================================================
-- SARI-SARI STORE INVENTORY SYSTEM
-- SQL DATABASE
-- =========================================================


-- =========================================================
-- 1. CREATE DATABASE
-- =========================================================

CREATE DATABASE sari_sari_store;

USE sari_sari_store;


-- =========================================================
-- 2. CREATE PRODUCT TABLE
-- =========================================================

CREATE TABLE PRODUCT (
    Product_ID INT PRIMARY KEY AUTO_INCREMENT,
    Product_Name VARCHAR(100) NOT NULL,
    Category VARCHAR(50) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    Quantity_In_Stock INT NOT NULL
);


-- =========================================================
-- 3. CREATE STAFF TABLE
-- =========================================================

CREATE TABLE STAFF (
    Staff_ID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(100) NOT NULL,
    Position VARCHAR(50) NOT NULL,
    Contact_Number VARCHAR(20)
);


-- =========================================================
-- 4. CREATE CUSTOMER TABLE
-- =========================================================

CREATE TABLE CUSTOMER (
    Customer_ID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(100) NOT NULL,
    Contact_Number VARCHAR(20)
);


-- =========================================================
-- 5. CREATE SALE TABLE
-- =========================================================

CREATE TABLE SALE (
    Sale_ID INT PRIMARY KEY AUTO_INCREMENT,
    Sale_Date DATETIME NOT NULL,
    Total_Amount DECIMAL(10,2) NOT NULL,
    Payment_Method VARCHAR(30) NOT NULL,

    Staff_ID INT,
    Customer_ID INT,

    FOREIGN KEY (Staff_ID)
        REFERENCES STAFF(Staff_ID),

    FOREIGN KEY (Customer_ID)
        REFERENCES CUSTOMER(Customer_ID)
);


-- =========================================================
-- 6. CREATE SALE_ITEM TABLE
-- =========================================================
-- SALE_ITEM resolves the many-to-many relationship
-- between SALE and PRODUCT.

CREATE TABLE SALE_ITEM (
    Sale_ID INT,
    Product_ID INT,
    Quantity INT NOT NULL,
    Unit_Price DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(10,2) NOT NULL,

    PRIMARY KEY (Sale_ID, Product_ID),

    FOREIGN KEY (Sale_ID)
        REFERENCES SALE(Sale_ID),

    FOREIGN KEY (Product_ID)
        REFERENCES PRODUCT(Product_ID)
);


-- =========================================================
-- 7. INSERT PRODUCT DATA
-- =========================================================

INSERT INTO PRODUCT
(Product_Name, Category, Price, Quantity_In_Stock)
VALUES
('Coca-Cola', 'Drinks', 25.00, 50),
('Lucky Me Pancit Canton', 'Food', 15.00, 100),
('Piattos', 'Snacks', 20.00, 40),
('Chippy', 'Snacks', 10.00, 30),
('Mineral Water', 'Drinks', 15.00, 60),
('SkyFlakes', 'Biscuits', 10.00, 50),
('Century Tuna', 'Canned Goods', 35.00, 25),
('Bear Brand', 'Milk', 15.00, 45);


-- =========================================================
-- 8. INSERT STAFF DATA
-- =========================================================

INSERT INTO STAFF
(Name, Position, Contact_Number)
VALUES
('Juan Dela Cruz', 'Cashier', '09123456789'),
('Maria Santos', 'Store Staff', '09234567890'),
('Pedro Reyes', 'Cashier', '09345678901');


-- =========================================================
-- 9. INSERT CUSTOMER DATA
-- =========================================================

INSERT INTO CUSTOMER
(Name, Contact_Number)
VALUES
('Ana Garcia', '09456789012'),
('Mark Santos', '09567890123'),
('John Reyes', '09678901234'),
('Sarah Cruz', '09789012345');


-- =========================================================
-- 10. INSERT SALES DATA
-- =========================================================

INSERT INTO SALE
(Sale_Date, Total_Amount, Payment_Method, Staff_ID, Customer_ID)
VALUES
('2026-09-23 09:30:00', 65.00, 'Cash', 1, 1),
('2026-09-23 10:15:00', 50.00, 'Cash', 2, 2),
('2026-09-23 11:00:00', 85.00, 'GCash', 1, 3),
('2026-09-23 13:30:00', 55.00, 'Cash', 3, 4);


-- =========================================================
-- 11. INSERT SALE ITEM DATA
-- =========================================================

INSERT INTO SALE_ITEM
(Sale_ID, Product_ID, Quantity, Unit_Price, Subtotal)
VALUES
(1, 1, 2, 25.00, 50.00),
(1, 4, 1, 10.00, 10.00),
(1, 2, 1, 15.00, 15.00),

(2, 3, 2, 20.00, 40.00),
(2, 4, 1, 10.00, 10.00),

(3, 1, 1, 25.00, 25.00),
(3, 2, 4, 15.00, 60.00),

(4, 7, 1, 35.00, 35.00),
(4, 6, 2, 10.00, 20.00);


-- =========================================================
-- 12. VIEW ALL PRODUCTS
-- =========================================================

SELECT *
FROM PRODUCT;


-- =========================================================
-- 13. VIEW ALL STAFF
-- =========================================================

SELECT *
FROM STAFF;


-- =========================================================
-- 14. VIEW ALL CUSTOMERS
-- =========================================================

SELECT *
FROM CUSTOMER;


-- =========================================================
-- 15. VIEW ALL SALES
-- =========================================================

SELECT *
FROM SALE;


-- =========================================================
-- 16. VIEW ALL SALE ITEMS
-- =========================================================

SELECT *
FROM SALE_ITEM;


-- =========================================================
-- 17. SEARCH FOR A PRODUCT
-- =========================================================

SELECT *
FROM PRODUCT
WHERE Product_Name = 'Coca-Cola';


-- =========================================================
-- 18. SEARCH PRODUCTS BY CATEGORY
-- =========================================================

SELECT *
FROM PRODUCT
WHERE Category = 'Snacks';


-- =========================================================
-- 19. FIND LOW-STOCK PRODUCTS
-- =========================================================

SELECT
    Product_ID,
    Product_Name,
    Category,
    Quantity_In_Stock
FROM PRODUCT
WHERE Quantity_In_Stock <= 10;


-- =========================================================
-- 20. ADD A NEW PRODUCT
-- =========================================================

INSERT INTO PRODUCT
(Product_Name, Category, Price, Quantity_In_Stock)
VALUES
('Jack n Jill Cream-O', 'Biscuits', 10.00, 40);


-- =========================================================
-- 21. UPDATE PRODUCT
-- =========================================================

UPDATE PRODUCT
SET Price = 12.00,
    Quantity_In_Stock = 45
WHERE Product_ID = 9;


-- =========================================================
-- 22. UPDATE STOCK AFTER A SALE
-- =========================================================

UPDATE PRODUCT
SET Quantity_In_Stock = Quantity_In_Stock - 2
WHERE Product_ID = 1;


-- =========================================================
-- 23. VIEW SALES WITH CUSTOMER AND STAFF
-- =========================================================

SELECT
    SALE.Sale_ID,
    SALE.Sale_Date,
    CUSTOMER.Name AS Customer_Name,
    STAFF.Name AS Staff_Name,
    SALE.Total_Amount,
    SALE.Payment_Method
FROM SALE
JOIN CUSTOMER
    ON SALE.Customer_ID = CUSTOMER.Customer_ID
JOIN STAFF
    ON SALE.Staff_ID = STAFF.Staff_ID;


-- =========================================================
-- 24. DETAILED SALES REPORT
-- =========================================================

SELECT
    SALE.Sale_ID,
    SALE.Sale_Date,
    CUSTOMER.Name AS Customer_Name,
    PRODUCT.Product_Name,
    SALE_ITEM.Quantity,
    SALE_ITEM.Unit_Price,
    SALE_ITEM.Subtotal
FROM SALE
JOIN CUSTOMER
    ON SALE.Customer_ID = CUSTOMER.Customer_ID
JOIN SALE_ITEM
    ON SALE.Sale_ID = SALE_ITEM.Sale_ID
JOIN PRODUCT
    ON SALE_ITEM.Product_ID = PRODUCT.Product_ID
ORDER BY SALE.Sale_ID;


-- =========================================================
-- 25. TOTAL SALES
-- =========================================================

SELECT
    SUM(Total_Amount) AS Total_Sales
FROM SALE;


-- =========================================================
-- 26. TOTAL NUMBER OF SALES
-- =========================================================

SELECT
    COUNT(Sale_ID) AS Total_Transactions
FROM SALE;


-- =========================================================
-- 27. TOTAL PRODUCTS SOLD
-- =========================================================

SELECT
    SUM(Quantity) AS Total_Products_Sold
FROM SALE_ITEM;


-- =========================================================
-- 28. TOTAL QUANTITY SOLD PER PRODUCT
-- =========================================================

SELECT
    PRODUCT.Product_ID,
    PRODUCT.Product_Name,
    SUM(SALE_ITEM.Quantity) AS Total_Quantity_Sold
FROM SALE_ITEM
JOIN PRODUCT
    ON SALE_ITEM.Product_ID = PRODUCT.Product_ID
GROUP BY
    PRODUCT.Product_ID,
    PRODUCT.Product_Name
ORDER BY Total_Quantity_Sold DESC;


-- =========================================================
-- 29. SALES PER STAFF
-- =========================================================

SELECT
    STAFF.Staff_ID,
    STAFF.Name AS Staff_Name,
    COUNT(SALE.Sale_ID) AS Number_Of_Sales,
    SUM(SALE.Total_Amount) AS Total_Sales
FROM STAFF
LEFT JOIN SALE
    ON STAFF.Staff_ID = SALE.Staff_ID
GROUP BY
    STAFF.Staff_ID,
    STAFF.Name;


-- =========================================================
-- 30. SALES PER CUSTOMER
-- =========================================================

SELECT
    CUSTOMER.Customer_ID,
    CUSTOMER.Name AS Customer_Name,
    COUNT(SALE.Sale_ID) AS Number_Of_Purchases,
    SUM(SALE.Total_Amount) AS Total_Spent
FROM CUSTOMER
LEFT JOIN SALE
    ON CUSTOMER.Customer_ID = SALE.Customer_ID
GROUP BY
    CUSTOMER.Customer_ID,
    CUSTOMER.Name;


-- =========================================================
-- 31. INVENTORY REPORT
-- =========================================================

SELECT
    Product_ID,
    Product_Name,
    Category,
    Price,
    Quantity_In_Stock,
    Price * Quantity_In_Stock AS Inventory_Value
FROM PRODUCT
ORDER BY Product_Name;


-- =========================================================
-- 32. LOW STOCK REPORT
-- =========================================================

SELECT
    Product_ID,
    Product_Name,
    Quantity_In_Stock
FROM PRODUCT
WHERE Quantity_In_Stock <= 10
ORDER BY Quantity_In_Stock ASC;


-- =========================================================
-- 33. DELETE A PRODUCT
-- =========================================================
-- Only use this when the product is not referenced
-- by SALE_ITEM.

-- DELETE FROM PRODUCT
-- WHERE Product_ID = 9;


-- =========================================================
-- END OF SQL DATABASE
-- =========================================================