-- Fix inventory for product ID 1 (desert rose)
-- Increase stock from 1 to 10 to allow cart quantity updates
UPDATE inventory_product 
SET count = 10 
WHERE product_id = 1;

-- Verify the update
SELECT * FROM inventory_product WHERE product_id = 1;
