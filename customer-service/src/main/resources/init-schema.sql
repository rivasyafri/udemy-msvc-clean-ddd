DROP SCHEMA IF EXISTS customer CASCADE;

CREATE SCHEMA CUSTOMER;

-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE customer.customers
(
    id uuid NOT NULL,
    username character varying COLLATE pg_catalog."default" NOT NULL,
    first_name character varying COLLATE pg_catalog."default" NOT NULL,
    last_name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);

DROP MATERIALIZED VIEW IF EXISTS CUSTOMER.order_customer_m_view;

CREATE MATERIALIZED VIEW CUSTOMER.order_customer_m_view
TABLESPACE pg_default
AS
    SELECT id,
           username,
           first_name,
           last_name
    FROM CUSTOMER.customers
WITH DATA;

REFRESH MATERIALIZED VIEW CUSTOMER.order_customer_m_view;

DROP FUNCTION IF EXISTS CUSTOMER.refresh_order_customer_m_view;

CREATE OR REPLACE FUNCTION CUSTOMER.refresh_order_customer_m_view()
RETURNS TRIGGER
AS '
BEGIN
    REFRESH MATERIALIZED VIEW CUSTOMER.order_customer_m_view;
    RETURN NULL;
END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS refresh_order_customer_m_view ON CUSTOMER.customers;

CREATE TRIGGER refresh_order_customer_m_view
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE
    ON CUSTOMER.customers FOR EACH STATEMENT
    EXECUTE PROCEDURE CUSTOMER.refresh_order_customer_m_view();