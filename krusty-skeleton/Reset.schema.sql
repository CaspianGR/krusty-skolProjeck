
DELETE FROM Customers;
DELETE FROM Orders;
DELETE FROM Cookies;
DELETE FROM Pallets;
DELETE FROM OrderLines;
DELETE FROM RawMaterials;
DELETE FROM Recipes;


INSERT INTO Customers (name, address) VALUES ('Bjudkakor AB', 'Ystad');
INSERT INTO Customers (name, address) VALUES ('Finkakor AB', 'Helsingborg');
INSERT INTO Customers (name, address) VALUES ('Gästkakor AB', 'Hässleholm');
INSERT INTO Customers (name, address) VALUES ('Kaffebröd AB', 'Landskrona');
INSERT INTO Customers (name, address) VALUES ('Kalaskakor AB', 'Trelleborg');
INSERT INTO Customers (name, address) VALUES ('Partykakor AB', 'Kristianstad');
INSERT INTO Customers (name, address) VALUES ('Skånekakor AB', 'Perstorp');
INSERT INTO Customers (name, address) VALUES ('Småbröd AB', 'Malmö');

INSERT INTO Cookies (name) VALUES ('Almond delight');
INSERT INTO Cookies (name) VALUES ('Amneris');
INSERT INTO Cookies (name) VALUES ('Berliner');
INSERT INTO Cookies (name) VALUES ('Nut cookie');
INSERT INTO Cookies (name) VALUES ('Nut ring');
INSERT INTO Cookies (name) VALUES ('Tango');

INSERT INTO RawMaterials (name, amount, unit) VALUES ('Bread crumbs', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Butter', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Chocolate', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Chopped almonds', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Cinnamon', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Egg whites', 500000, 'ml');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Eggs', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Fine-ground nuts', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Flour', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Ground, roasted nuts', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Icing sugar', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Marzipan', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Potato starch', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Roasted, chopped nuts', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Sodium bicarbonate', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Sugar', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Vanilla sugar', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Vanilla', 500000, 'g');
INSERT INTO RawMaterials (name, amount, unit) VALUES ('Wheat flour', 500000, 'g');

-- Almond delight
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Almond delight', 'Butter', 400, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Almond delight', 'Chopped almonds', 279, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Almond delight', 'Cinnamon', 10, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Almond delight', 'Flour', 400, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Almond delight', 'Sugar', 270, 'g');

-- Amneris
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Amneris', 'Butter', 250, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Amneris', 'Eggs', 250, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Amneris', 'Marzipan', 750, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Amneris', 'Potato starch', 25, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Amneris', 'Wheat flour', 25, 'g');

-- Berliner
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Berliner', 'Butter', 250, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Berliner', 'Chocolate', 50, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Berliner', 'Eggs', 50, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Berliner', 'Flour', 350, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Berliner', 'Icing sugar', 100, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Berliner', 'Vanilla sugar', 5, 'g');

-- Nut cookie
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut cookie', 'Bread crumbs', 125, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut cookie', 'Chocolate', 50, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut cookie', 'Egg whites', 350, 'ml');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut cookie', 'Fine-ground nuts', 750, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut cookie', 'Ground, roasted nuts', 625, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut cookie', 'Sugar', 375, 'g');

-- Nut ring
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut ring', 'Butter', 450, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut ring', 'Flour', 450, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut ring', 'Icing sugar', 190, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Nut ring', 'Roasted, chopped nuts', 225, 'g');

-- Tango
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Tango', 'Butter', 200, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Tango', 'Flour', 300, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Tango', 'Sodium bicarbonate', 4, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Tango', 'Sugar', 250, 'g');
INSERT INTO Recipes (cookie, raw_material, amount, unit) VALUES ('Tango', 'Vanilla', 2, 'g');