-- 移除 wms2test delete_flag 逻辑删除字段，并切换为物理删除
USE wms2test;

-- 1. 先清理历史逻辑删除数据
DELETE FROM io_order_detail WHERE delete_flag <> 0;
DELETE FROM io_apply_detail WHERE delete_flag <> 0;
DELETE FROM product_stock_detail WHERE delete_flag <> 0;
DELETE FROM location_transfer WHERE delete_flag <> 0;
DELETE FROM io_order WHERE delete_flag <> 0;
DELETE FROM io_apply WHERE delete_flag <> 0;
DELETE FROM product WHERE delete_flag <> 0;
DELETE FROM product_category WHERE delete_flag <> 0;
DELETE FROM product_unit WHERE delete_flag <> 0;
DELETE FROM product_location WHERE delete_flag <> 0;
DELETE FROM customer WHERE delete_flag <> 0;
DELETE FROM deliveryman WHERE delete_flag <> 0;
DELETE FROM io_type WHERE delete_flag <> 0;
DELETE FROM salesman WHERE delete_flag <> 0;
DELETE FROM warehouse WHERE delete_flag <> 0;
DELETE FROM sys_user WHERE delete_flag <> 0;

-- 2. 删除依赖 delete_flag 的唯一索引
DROP INDEX uk_product_category_name ON product_category;
DROP INDEX uk_product_unit_name ON product_unit;
DROP INDEX uk_product_location_code ON product_location;
DROP INDEX uk_product_code ON product;
DROP INDEX uk_product_barcode ON product;
DROP INDEX uk_product_model ON product;
DROP INDEX uk_io_type_name ON io_type;
DROP INDEX uk_io_apply_no ON io_apply;
DROP INDEX uk_io_order_no ON io_order;
DROP INDEX uk_product_stock_detail_product_location ON product_stock_detail;
DROP INDEX uk_warehouse_name ON warehouse;

-- 3. 删除 delete_flag 字段
ALTER TABLE product_category DROP COLUMN delete_flag;
ALTER TABLE product_unit DROP COLUMN delete_flag;
ALTER TABLE product_location DROP COLUMN delete_flag;
ALTER TABLE product DROP COLUMN delete_flag;
ALTER TABLE customer DROP COLUMN delete_flag;
ALTER TABLE deliveryman DROP COLUMN delete_flag;
ALTER TABLE io_type DROP COLUMN delete_flag;
ALTER TABLE io_apply DROP COLUMN delete_flag;
ALTER TABLE io_apply_detail DROP COLUMN delete_flag;
ALTER TABLE io_order DROP COLUMN delete_flag;
ALTER TABLE io_order_detail DROP COLUMN delete_flag;
ALTER TABLE product_stock_detail DROP COLUMN delete_flag;
ALTER TABLE location_transfer DROP COLUMN delete_flag;
ALTER TABLE sys_user DROP COLUMN delete_flag;
ALTER TABLE salesman DROP COLUMN delete_flag;
ALTER TABLE warehouse DROP COLUMN delete_flag;

-- 4. 重建真实业务唯一索引
CREATE UNIQUE INDEX uk_product_category_name ON product_category(name);
CREATE UNIQUE INDEX uk_product_unit_name ON product_unit(name);
CREATE UNIQUE INDEX uk_product_location_code ON product_location(code);
CREATE UNIQUE INDEX uk_product_code ON product(code);
CREATE UNIQUE INDEX uk_product_barcode ON product(barcode);
CREATE UNIQUE INDEX uk_product_model ON product(model);
CREATE UNIQUE INDEX uk_io_type_name ON io_type(name, scope);
CREATE UNIQUE INDEX uk_io_apply_no ON io_apply(apply_no);
CREATE UNIQUE INDEX uk_io_order_no ON io_order(order_no);
CREATE UNIQUE INDEX uk_product_stock_detail_product_location ON product_stock_detail(product_id, location_id);
CREATE UNIQUE INDEX uk_warehouse_name ON warehouse(name);
