-- 出库申请
CREATE TABLE IF NOT EXISTS outbound_apply (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  apply_no VARCHAR(32) NOT NULL COMMENT '申请编号，如CS000001',
  apply_date DATE NOT NULL COMMENT '申请日期',
  applicant_name VARCHAR(64) NULL COMMENT '申请人',
  deliveryman_id BIGINT NULL COMMENT '送货员ID',
  customer_id BIGINT NULL COMMENT '客户ID',
  outbound_type_id BIGINT NULL COMMENT '出入库类型ID（出库）',
  remark VARCHAR(255) NULL COMMENT '备注',
  approve_status TINYINT NOT NULL DEFAULT 0 COMMENT '是否审批：0-未审批 1-已审批',
  outbound_status TINYINT NOT NULL DEFAULT 0 COMMENT '是否出库：0-未出库 1-已出库',
  approved_time TIMESTAMP NULL COMMENT '审批时间',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  delete_flag BIGINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='出库申请';

CREATE UNIQUE INDEX uk_outbound_apply_no ON outbound_apply(apply_no, delete_flag);
CREATE INDEX idx_outbound_apply_date ON outbound_apply(apply_date);
CREATE INDEX idx_outbound_apply_status ON outbound_apply(approve_status, outbound_status);
