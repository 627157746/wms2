-- 入库单明细
CREATE TABLE IF NOT EXISTS inbound_order_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  order_id BIGINT NOT NULL COMMENT '入库单ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  qty DECIMAL(18,2) NOT NULL COMMENT '数量',
  location_id BIGINT NULL COMMENT '货位ID',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='入库单明细';

CREATE INDEX idx_inbound_order_detail_order ON inbound_order_detail(order_id);
CREATE INDEX idx_inbound_order_detail_product ON inbound_order_detail(product_id);
CREATE INDEX idx_inbound_order_detail_location ON inbound_order_detail(location_id);
