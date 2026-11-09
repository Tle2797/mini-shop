-- ใส่สินค้าตัวอย่าง "เฉพาะตอนตารางยังว่าง" เพื่อให้ restart กี่ครั้งข้อมูลก็ไม่ซ้ำ
INSERT INTO products (name, description, price, stock)
SELECT v.name, v.description, v.price, v.stock
FROM (VALUES
    ('Mechanical Keyboard', 'คีย์บอร์ด Blue Switch มีไฟ RGB',   1590.00, 25),
    ('Wireless Mouse',      'เมาส์ไร้สาย 2.4GHz แบตอึด',        490.00,  40),
    ('USB-C Hub 6-in-1',    'HDMI 4K, USB 3.0 x3, SD, PD 100W', 890.00,  15),
    ('27" Monitor',         'จอ IPS 2K 75Hz',                    6990.00, 8),
    ('Laptop Stand',        'ขาตั้งโน้ตบุ๊กอะลูมิเนียม ปรับระดับได้', 650.00, 30)
) AS v(name, description, price, stock)
WHERE NOT EXISTS (SELECT 1 FROM products);