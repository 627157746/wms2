-- 商品增加期初库存货位ID
ALTER TABLE product
  ADD COLUMN initial_stock_location_id BIGINT NULL COMMENT '期初库存货位ID，为空表示无货位' AFTER initial_stock;

CREATE INDEX idx_product_initial_stock_location ON product(initial_stock_location_id);
