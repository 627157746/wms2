-- 出库申请明细
CREATE TABLE IF NOT EXISTS outbound_apply_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  apply_id BIGINT NOT NULL COMMENT '出库申请ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  qty DECIMAL(18,2) NOT NULL COMMENT '数量',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='出库申请明细';

CREATE INDEX idx_outbound_apply_detail_apply ON outbound_apply_detail(apply_id);
CREATE INDEX idx_outbound_apply_detail_product ON outbound_apply_detail(product_id);
