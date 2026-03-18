-- 商品
CREATE TABLE IF NOT EXISTS product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  name VARCHAR(100) NOT NULL COMMENT '商品名称',
  code VARCHAR(50) NOT NULL COMMENT '商品编号',
  barcode VARCHAR(50) NULL COMMENT '条形码',
  unit_id BIGINT NOT NULL COMMENT '单位ID',
  category_id BIGINT NULL COMMENT '分类ID',
  min_stock DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '最低库存',
  initial_stock DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '期初库存',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品';

CREATE UNIQUE INDEX uk_product_code ON product(code);
CREATE INDEX idx_product_name ON product(name);
CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_unit ON product(unit_id);
