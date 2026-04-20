SET DATABASE SQL LOWER CASE IDENTIFIER TRUE;

CREATE TABLE IF NOT EXISTS Customers(
	name			VARCHAR(100),
	address			VARCHAR(200),

	PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS Orders(
	id				INTEGER IDENTITY NOT NULL,
	customer_name	VARCHAR(100) NOT NULL,
	order_made_at	TIMESTAMP NOT NULL,
	desired_delivery_date	DATE,

	FOREIGN KEY (customer_name) REFERENCES Customers(name)
);

CREATE TABLE IF NOT EXISTS Cookies(
	name			VARCHAR(100),

	PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS Pallets(
	id				INTEGER IDENTITY NOT NULL,
	cookie			VARCHAR(100) NOT NULL,
	production_date	TIMESTAMP NOT NULL,
	order_id		INTEGER,
	delivered_at	TIMESTAMP,
	blocked			BOOLEAN NOT NULL,

	FOREIGN KEY (order_id) REFERENCES Orders(id),
	FOREIGN KEY (cookie) REFERENCES Cookies(name)
);

CREATE TABLE IF NOT EXISTS OrderLines(
	order_id		INTEGER NOT NULL,
	cookie			VARCHAR(100) NOT NULL,
	pallets			INTEGER NOT NULL,

	PRIMARY KEY (order_id, cookie),
	FOREIGN KEY (order_id) REFERENCES Orders(id),
	FOREIGN KEY (cookie) REFERENCES Cookies(name)
);

CREATE TABLE IF NOT EXISTS RawMaterials(
	name			VARCHAR(100) NOT NULL,
	amount			INTEGER NOT NULL,
	unit			VARCHAR(10) NOT NULL,
	last_order_amount	INTEGER, -- NOT NULL,
	last_order_time	TIMESTAMP, -- NOT NULL,

	PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS Recipes(
	cookie			VARCHAR(100) NOT NULL,
	raw_material	VARCHAR(100) NOT NULL,
	amount			INTEGER NOT NULL,
	unit			VARCHAR(10) NOT NULL,

	PRIMARY KEY (cookie, raw_material),
	FOREIGN KEY (cookie) REFERENCES Cookies(name),
	FOREIGN KEY (raw_material) REFERENCES RawMaterials(name)
);