-- 出入库申请
CREATE TABLE IF NOT EXISTS io_apply (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  apply_no VARCHAR(32) NOT NULL COMMENT '申请单号，如RS000003/CS000001',
  order_type TINYINT NOT NULL COMMENT '单据类型：1-入库 2-出库',
  apply_date DATE NOT NULL COMMENT '申请日期',
  deliveryman_id BIGINT NULL COMMENT '送货员ID',
  customer_id BIGINT NULL COMMENT '客户ID，仅出库使用',
  salesman_id BIGINT NULL COMMENT '业务员ID，仅出库使用',
  io_type_id BIGINT NULL COMMENT '出入库类型ID',
  remark VARCHAR(255) NULL COMMENT '备注',
  approve_status TINYINT NOT NULL DEFAULT 0 COMMENT '审批状态：0-未审批 1-已审批',
  io_status TINYINT NOT NULL DEFAULT 0 COMMENT '出入库状态：0-未执行 1-已执行',
  approved_time TIMESTAMP NULL COMMENT '审批时间',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='出入库申请';

CREATE UNIQUE INDEX uk_io_apply_no ON io_apply(apply_no, delete_flag);
CREATE INDEX idx_io_apply_type_date ON io_apply(order_type, apply_date);
CREATE INDEX idx_io_apply_status ON io_apply(order_type, approve_status, io_status);
