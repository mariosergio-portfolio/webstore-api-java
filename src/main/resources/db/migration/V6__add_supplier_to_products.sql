-- Sample suppliers (one per city) and link every product to a supplier.
-- products.supplier_id stays nullable at the DB level because the JPA product
-- entity does not know about suppliers yet.
INSERT INTO suppliers (id, name, email, address_city_id) VALUES
    ('5d0e1a20-0000-4000-8000-000000000001', 'Shenzhen Tech Supply Co.',   'sales@shenzhen-tech.example',  '7a1b0c10-0000-4000-8000-000000000002'),
    ('5d0e1a20-0000-4000-8000-000000000002', 'Berlin Books Wholesale GmbH', 'orders@berlin-books.example', '7a1b0c10-0000-4000-8000-000000000004'),
    ('5d0e1a20-0000-4000-8000-000000000003', 'Austin Home Goods LLC',      'hello@austin-home.example',    '7a1b0c10-0000-4000-8000-000000000003'),
    ('5d0e1a20-0000-4000-8000-000000000004', 'Osaka Outdoor Gear KK',      'info@osaka-outdoor.example',   '7a1b0c10-0000-4000-8000-000000000005'),
    ('5d0e1a20-0000-4000-8000-000000000005', 'São Paulo Toys Ltda',        'vendas@sp-toys.example',       '7a1b0c10-0000-4000-8000-000000000001');

ALTER TABLE products ADD COLUMN supplier_id UUID REFERENCES suppliers(id);
CREATE INDEX idx_products_supplier_id ON products(supplier_id);

UPDATE products SET supplier_id = '5d0e1a20-0000-4000-8000-000000000001'
 WHERE category_id = (SELECT id FROM categories WHERE slug = 'electronics');
UPDATE products SET supplier_id = '5d0e1a20-0000-4000-8000-000000000002'
 WHERE category_id = (SELECT id FROM categories WHERE slug = 'books');
UPDATE products SET supplier_id = '5d0e1a20-0000-4000-8000-000000000003'
 WHERE category_id = (SELECT id FROM categories WHERE slug = 'home-kitchen');
UPDATE products SET supplier_id = '5d0e1a20-0000-4000-8000-000000000004'
 WHERE category_id = (SELECT id FROM categories WHERE slug = 'sports-outdoors');
UPDATE products SET supplier_id = '5d0e1a20-0000-4000-8000-000000000005'
 WHERE category_id = (SELECT id FROM categories WHERE slug = 'toys-games');
