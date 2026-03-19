-- 库存明细
CREATE TABLE IF NOT EXISTS inventory_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  location_id BIGINT NOT NULL COMMENT '货位ID',
  qty BIGINT NOT NULL DEFAULT 0 COMMENT '库存数量',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存明细';

CREATE UNIQUE INDEX uk_inventory_detail_product_location ON inventory_detail(product_id, location_id, delete_flag);
CREATE INDEX idx_inventory_detail_location ON inventory_detail(location_id);
