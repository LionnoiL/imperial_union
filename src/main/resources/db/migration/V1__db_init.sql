CREATE TABLE products (
	id varchar(36) NOT NULL,
	product_name varchar(200) NOT NULL,
	weight bool,
	CONSTRAINT products_pkey PRIMARY KEY (id),
	UNIQUE INDEX product_name_indx (product_name)
);

CREATE TABLE barcodes (
	product_id varchar(36),
	barcode varchar(100),
	CONSTRAINT barcodes_pkey PRIMARY KEY (product_id, barcode)
);

CREATE TABLE shop_products (
	product_id varchar(36),
	product_code varchar(20),
	shop_id INT,
	CONSTRAINT shop_products_pkey PRIMARY KEY (product_id, product_code)
);

CREATE TABLE similarity_products (
	product_id_1 varchar(36),
	product_id_2 varchar(36),
	CONSTRAINT similarity_products_pkey PRIMARY KEY (product_id_1, product_id_2)
);