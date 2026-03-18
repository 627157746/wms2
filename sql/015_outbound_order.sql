-- 出库单
CREATE TABLE IF NOT EXISTS outbound_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  order_no VARCHAR(32) NOT NULL COMMENT '出库单号，如CK000002',
  apply_id BIGINT NULL COMMENT '来源出库申请ID，可为空',
  outbound_date DATE NOT NULL COMMENT '出库日期',
  deliveryman_id BIGINT NULL COMMENT '送货员ID',
  customer_id BIGINT NULL COMMENT '客户ID',
  outbound_type_id BIGINT NULL COMMENT '出库类型ID',
  remark VARCHAR(255) NULL COMMENT '备注',
  picking_status TINYINT NOT NULL DEFAULT 0 COMMENT '拣货状态：0-未拣 1-已拣',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='出库单';

CREATE UNIQUE INDEX uk_outbound_order_no ON outbound_order(order_no);
CREATE INDEX idx_outbound_order_date ON outbound_order(outbound_date);
