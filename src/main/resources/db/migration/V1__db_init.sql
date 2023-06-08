CREATE TABLE products (
	id varchar(36) NOT NULL,
	product_name varchar(200) NOT NULL,
	weight bool,
	complete_shop bool default 0,
	complete_name bool default 0,
	CONSTRAINT products_pkey PRIMARY KEY (id)
);

CREATE TABLE barcodes (
	product_id varchar(36),
	barcode varchar(100),
	CONSTRAINT barcodes_pkey PRIMARY KEY (product_id, barcode),
	CONSTRAINT barcodes_fk_products FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE shop_products (
	product_id varchar(36),
	product_code varchar(20),
	shop_id INT,
	CONSTRAINT shop_products_pkey PRIMARY KEY (product_id, product_code, shop_id),
	CONSTRAINT shop_products_fk_products FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE similarity_products (
	product_id_1 varchar(36),
	product_id_2 varchar(36),
	CONSTRAINT similarity_products_pkey PRIMARY KEY (product_id_1, product_id_2),
	CONSTRAINT similarity_products_1_fk_products FOREIGN KEY (product_id_1) REFERENCES products(id),
	CONSTRAINT similarity_products_2_fk_products FOREIGN KEY (product_id_2) REFERENCES products(id)
);