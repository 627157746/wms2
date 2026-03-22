-- 转货位记录
CREATE TABLE IF NOT EXISTS location_transfer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  from_location_id BIGINT NOT NULL COMMENT '原货位ID，0表示无货位',
  to_location_id BIGINT NOT NULL COMMENT '转移货位ID，0表示无货位',
  transfer_qty BIGINT NOT NULL DEFAULT 0 COMMENT '转移数量',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='转货位记录';

CREATE INDEX idx_location_transfer_product ON location_transfer(product_id);
CREATE INDEX idx_location_transfer_from_location ON location_transfer(from_location_id);
CREATE INDEX idx_location_transfer_to_location ON location_transfer(to_location_id);
