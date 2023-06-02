CREATE TABLE products (
	id BIGINT NOT NULL AUTO_INCREMENT,
	product_name varchar(200) NOT NULL,
	weight bool,
	CONSTRAINT products_pkey PRIMARY KEY (id)
);

CREATE TABLE barcodes (
	product_id BIGINT,
	barcode varchar(13),
	CONSTRAINT barcodes_pkey PRIMARY KEY (product_id, barcode)
);

CREATE TABLE shop_products (
	product_id BIGINT,
	product_code varchar(20),
	CONSTRAINT shop_products_pkey PRIMARY KEY (product_id, product_code)
);