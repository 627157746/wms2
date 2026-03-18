-- 出入库单
CREATE TABLE IF NOT EXISTS io_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  order_no VARCHAR(32) NOT NULL COMMENT '单号，如RK000001/CK000001',
  order_type TINYINT NOT NULL COMMENT '单据类型：1-入库 2-出库',
  apply_id BIGINT NULL COMMENT '来源申请ID，可为空',
  biz_date DATE NOT NULL COMMENT '业务日期',
  deliveryman_id BIGINT NULL COMMENT '送货员ID',
  customer_id BIGINT NULL COMMENT '客户ID，仅出库使用',
  io_type_id BIGINT NULL COMMENT '出入库类型ID',
  remark VARCHAR(255) NULL COMMENT '备注',
  picking_status TINYINT NOT NULL DEFAULT 0 COMMENT '拣货状态：0-未拣 1-已拣，仅出库使用',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='出入库单';

CREATE UNIQUE INDEX uk_io_order_no ON io_order(order_no, delete_flag);
CREATE INDEX idx_io_order_type_date ON io_order(order_type, biz_date);
