DROP SCHEMA IF EXISTS cis CASCADE;
CREATE SCHEMA cis;

DROP SCHEMA IF EXISTS pc CASCADE;
CREATE SCHEMA pc;

DROP SCHEMA IF EXISTS us CASCADE;
CREATE SCHEMA us;

DROP TABLE IF EXISTS cis.customer_types CASCADE;
DROP TABLE IF EXISTS cis.customer CASCADE;
DROP TABLE IF EXISTS cis.account_type CASCADE;
DROP TABLE IF EXISTS cis.customer_accounts CASCADE;
DROP TABLE IF EXISTS cis.document CASCADE;
DROP TABLE IF EXISTS cis.customer_documents CASCADE;

CREATE TABLE cis.customer_types (
    customer_types_id SERIAL PRIMARY KEY,
    description varchar(255) NOT NULL,
    name varchar(255) NOT NULL
);

CREATE TABLE cis.customer (
    customer_id SERIAL PRIMARY KEY,
    email varchar(255) NOT NULL,
    first_name varchar(255),
    id_number varchar(255) NOT NULL,
    last_name varchar(255),
    password varchar(255) NOT NULL,
    role varchar(255) NOT NULL,
    customer_types_id int8,
    FOREIGN KEY (customer_types_id) REFERENCES cis.customer_types(customer_types_id)
);

CREATE TABLE cis.account_type (
    account_type_id SERIAL PRIMARY KEY,
    description varchar(255) NOT NULL,
    name varchar(255) NOT NULL
);

CREATE TABLE cis.customer_accounts (
    customer_accounts_id SERIAL PRIMARY KEY,
    account_type_id int8,
    customer_id int8,
    FOREIGN KEY (customer_id) REFERENCES cis.customer(customer_id),
    FOREIGN KEY (account_type_id) REFERENCES cis.account_type(account_type_id)
);

CREATE TABLE cis.document (
    document_id SERIAL PRIMARY KEY,
    document bytea NOT NULL
);

CREATE TABLE cis.customer_documents (
    customer_documents_id SERIAL PRIMARY KEY,
    customer_id int8,
    document_id int8,
    FOREIGN KEY (document_id) REFERENCES cis.document(document_id),
    FOREIGN KEY (customer_id) REFERENCES cis.customer(customer_id)
);

INSERT INTO cis.customer_types(customer_types_id, description, name)
VALUES(1,'Customer Type for Individual Banking','INDIVIDUAL'),
       (2,'Sole Prop Customer Type','SOLE PROP'),
       (3,'Customer Type for Non-Profit','NON-PROFIT'),
       (4,'Customer Type for CIPC Registered Customers','CIPC'),
       (5,'Customer Type for System-To-System integration','SYSTEM');

INSERT INTO cis.customer(customer_id, email, first_name, id_number, last_name, password, role, customer_types_id)
VALUES (1,'jesse.leresche@entelect.co.za','Jesse','9001010000081','Leresche','$2a$10$7BzgqOtWTh5S4VY/gWDJfupyOXXndhizmhneVgeuBrhDVKicZfYRe','ADMIN',1),
       (2,'Ahmad@entelect.co.za','Ahmad','2807197000081','Mahomed','$2a$10$QYjASzu0JVwET6i1dUwuueJ3a9Qzm46sZkebZzyVre0sACax6x9aK','ADMIN',1),
       (3,'admin@entelect.co.za','admin','','admin','$2a$10$FlBVaadiSi9X//j.z/8fb.EWzBTEOY1aGjH4zTo/w3j8daOSGmAky','ADMIN',5);


INSERT INTO cis.account_type(account_type_id, description, name)
VALUES(1,'Cheque Account for People with a little bit of money','Gold Cheque Account'),
       (2,'Cheque Account for People with a little bit of money','Platinum Cheque Account'),
       (3,'Cheque Account for People with a little bit of money','Signet Cheque Account'),
       (4,'Cheque Account for Islamic Banking Customers','Islamic Cheque Account'),
       (5,'Savings Account for Individuals','Savings Account'),
       (6,'Cheque Account for Small Businesses','SME Checking Account'),
       (7,'Cheque Account for Medium Businesses','Medium Enterprise Checking Account'),
       (8,'Cheque Account for Large Businesses','Large Enterprise Checking Account');

INSERT INTO cis.customer_accounts(customer_accounts_id, account_type_id, customer_id)
VALUES (1,1,1),
       (2,5,1),
       (3,4,2);

insert into cis.document( document_id, document)
values(1,'data:image/jpeg;base64,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'),
      (2,'data:image/jpeg;base64,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'
);

insert into cis.customer_documents(customer_documents_id, customer_id, document_id)
values(1,1,1),
      (2,1,2);

DROP TABLE IF EXISTS pc.qualifying_customer_types CASCADE;
DROP TABLE IF EXISTS pc.qualifying_accounts CASCADE;
DROP TABLE IF EXISTS pc.fulfilment_type CASCADE;
DROP TABLE IF EXISTS pc.orders CASCADE;
DROP TABLE IF EXISTS pc.order_items CASCADE;
DROP TABLE IF EXISTS pc.products CASCADE;
DROP TABLE IF EXISTS pc.cart CASCADE;
DROP TABLE IF EXISTS pc.cart_item CASCADE;
DROP TABLE IF EXISTS pc.checks_status CASCADE;

--CREATE ALL NEW REQUIRED TABLES
CREATE TABLE pc.products (
  product_id SERIAL PRIMARY KEY,
  name TEXT,
  description TEXT,
  price DECIMAL,
  image_url TEXT
);

CREATE TABLE pc.orders (
  order_id SERIAL PRIMARY KEY,
  customer_id INT NOT NULL REFERENCES cis.customer(customer_id),
  createdAt TIMESTAMP,
  status TEXT,
  contract_url TEXT
);

CREATE TABLE pc.order_items (
  order_items_id SERIAL PRIMARY KEY,
  product_id INT NOT NULL REFERENCES pc.products(product_id),
  order_id INT NOT NULL REFERENCES pc.orders(order_id),
  description TEXT
);

CREATE TABLE pc.qualifying_customer_types (
  qualifying_customer_types_id SERIAL PRIMARY KEY,
  product_id INT NOT NULL REFERENCES pc.products(product_id),
  customer_types_id INT NOT NULL REFERENCES cis.customer_types(customer_types_id)
);

CREATE TABLE pc.qualifying_accounts (
  qualifying_account_id SERIAL PRIMARY KEY,
  account_type_id INT NOT NULL REFERENCES cis.account_type(account_type_id),
  product_id INT NOT NULL REFERENCES pc.products(product_id)
);

CREATE TABLE pc.fulfilment_type (
  fulfilment_type_id SERIAL PRIMARY KEY,
  product_id INT NOT NULL REFERENCES pc.products(product_id),
  description TEXT,
  name TEXT
);

CREATE TABLE IF NOT EXISTS pc.cart (
    cart_id SERIAL PRIMARY KEY,
    customer_id INT8 NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES cis.customer(customer_id)
);

CREATE TABLE IF NOT EXISTS pc.cart_item (
    cart_item_id SERIAL PRIMARY KEY,
    cart_id INT8 NOT NULL,
    product_id INT8 NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES pc.cart(cart_id),
    FOREIGN KEY (product_id) REFERENCES pc.products(product_id)
);

CREATE TABLE IF NOT EXISTS pc.checks_status (
    check_status_id SERIAL PRIMARY KEY,
    order_items_id INT8 NOT NULL,
    status VARCHAR(255) NOT NULL,
    description TEXT,
    FOREIGN KEY (order_items_id) REFERENCES pc.order_items(order_items_id)
);

--INSERTION SCRIPTS
--------------------------------------
--Products Table
insert into pc.products (name, description, price)
values ('Retail Short Term Insurance', 'Provides cover for short-term products for individuals - Electronics,
Household Items, Jewellery, Cars etc.' , 500);

insert into pc.products (name, description, price)
values ('Retail Long-Term Insurance', 'Provides cover for longer term products individuals - household
insurance, life insurance etc.' ,1000);

insert into pc.products (name, description, price)
values ('Commercial Short Term Insurance', 'Provides cover for short-term products for individuals - Electronics,
Household Items, Jewellery, Cars etc.' ,5000);

insert into pc.products (name, description, price)
values ('Commercial Long-Term Insurance', 'Provides cover for longer term products - office insurance,
employee benefit insurance, etc.', 10000);

insert into pc.products (name, description, price)
values ('Device Contract', 'Allows the customer to take out a device on contract - such as a
phone, laptop etc.' ,850);

insert into pc.products (name, description, price)
values ('Short-Term Investment Product', 'Provides a way for customers to invest their money over a short
period of time - 32 day fixed deposit etc.' ,2500);

insert into pc.products (name, description, price)
values ('Long-Term Investment Product', 'Provides a way for users to invest their money over the long term -
Retirement / Annuity Funds, Unit Trusts etc.' ,5000);

insert into pc.products (name, description, price)
values ('Islamic Investment Product', 'Provides a way for Islamic customers to invest their money.' ,5000);

insert into pc.products (name, description, price)
values ('VIP Investment Product', 'Provides an Investment product for VIP customers Over 150 Million
Net-Asset Value.' ,20000);

--------------------------------------
--Qualifying Accounts Table
insert into pc.qualifying_accounts (product_id, account_type_id)
values (1, 1);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (1, 2);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (1, 3);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (1, 3);

-- Second Entry
insert into pc.qualifying_accounts (product_id, account_type_id)
values (2, 1);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (2, 2);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (2, 3);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (2, 4);

-- Third Entry
insert into pc.qualifying_accounts (product_id, account_type_id)
values (3, 6);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (3, 7);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (3, 8);

-- Forth Entry
insert into pc.qualifying_accounts (product_id, account_type_id)
values (4, 6);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (4, 7);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (4, 8);

-- Fifth Entry
insert into pc.qualifying_accounts (product_id, account_type_id)
values (5, 1);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (5, 2);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (5, 3);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (5, 4);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (5, 5);

-- Sixth Entry 021204
insert into pc.qualifying_accounts (product_id, account_type_id)
values (6, 1);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (6, 2);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (6, 4);

--Seventh Entry
insert into pc.qualifying_accounts (product_id, account_type_id)
values (7, 1);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (7, 2);
insert into pc.qualifying_accounts (product_id, account_type_id)
values (7, 4);

-- Eighth Entry
insert into pc.qualifying_accounts (product_id, account_type_id)
values (8, 4);

-- Ninth Entry
insert into pc.qualifying_accounts (product_id, account_type_id)
values (9, 3);

--------------------------------------
--Fulfilment Type Table
insert into pc.fulfilment_type (name, description, product_id)
values ('A', 'Smallest unit of checks. This Just requires KYC to be completed.', 5);

insert into pc.fulfilment_type (name, description, product_id)
values ('B', 'Requires all of the checks in Type A, as well as the Fraud Check, Living Status Check and Duplicate ID Check.', 6);

insert into pc.fulfilment_type (name, description, product_id)
values ('B', 'Requires all of the checks in Type A, as well as the Fraud Check, Living Status Check and Duplicate ID Check.',7 );

insert into pc.fulfilment_type (name, description, product_id)
values ('B', 'Requires all of the checks in Type A, as well as the Fraud Check, Living Status Check and Duplicate ID Check.',8);

insert into pc.fulfilment_type (name, description, product_id)
values ('B', 'Requires all of the checks in Type A, as well as the Fraud Check, Living Status Check and Duplicate ID Check.',9);

insert into pc.fulfilment_type (name, description, product_id)
values ('C', 'Requires all of the checks in Type B, as well as the Marital Status Check & Credit Check.', 1);

insert into pc.fulfilment_type (name, description, product_id)
values ('C', 'Requires all of the checks in Type B, as well as the Marital Status Check & Credit Check.', 2);

insert into pc.fulfilment_type (name, description, product_id)
values ('C', 'Requires all of the checks in Type B, as well as the Marital Status Check & Credit Check.', 3);

insert into pc.fulfilment_type (name, description, product_id)
values ('C', 'Requires all of the checks in Type B, as well as the Marital Status Check & Credit Check.', 4);
--------------------------------------
--Customer Types Table
-- First
insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (1, 1);


-- Second
insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (2, 1);


-- Third
insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (3, 2);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (3, 3);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (3, 4);


-- Forth
insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (4, 2);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (4, 3);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (4, 4);



-- Fifth
insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (5, 1);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (5, 2);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (5, 3);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (5, 4);



-- Sixth
insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (6, 1);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (6, 2);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (6, 3);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (6, 4);

-- Seventh
insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (7, 1);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (7, 2);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (7, 3);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (7, 4);
-- Eighth
insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (8, 1);

insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (8, 3);
-- Ninth
insert into pc.qualifying_customer_types (product_id, customer_types_id)
values (9, 1);


DROP TABLE IF EXISTS us.users CASCADE;
DROP TABLE IF EXISTS us.checks_history CASCADE;

CREATE TABLE IF NOT EXISTS us.users (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS us.checks_history (
    check_history_id SERIAL PRIMARY KEY,
    customer_id INT8 NOT NULL,
    check_type VARCHAR(255) NOT NULL,
    check_date DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES cis.customer(customer_id)
);

INSERT INTO us.users (email, password)
SELECT email, password FROM cis.customer;