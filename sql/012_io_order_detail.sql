-- 出入库记录
CREATE TABLE IF NOT EXISTS io_order_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  order_id BIGINT NOT NULL COMMENT '出入库单ID',
  order_type TINYINT NOT NULL COMMENT '单据类型：1-入库 2-出库',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  qty BIGINT NOT NULL COMMENT '数量',
  location_id BIGINT NULL COMMENT '货位ID',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='出入库记录';

CREATE INDEX idx_io_order_detail_order ON io_order_detail(order_type, order_id);
CREATE INDEX idx_io_order_detail_product ON io_order_detail(product_id);
CREATE INDEX idx_io_order_detail_location ON io_order_detail(location_id);
