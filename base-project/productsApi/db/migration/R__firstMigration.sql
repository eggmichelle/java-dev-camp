--DROP SCHEMA IF EXISTS pc;
--CREATE SCHEMA pc;
--
--DROP SCHEMA IF EXISTS us;
--CREATE SCHEMA us;

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