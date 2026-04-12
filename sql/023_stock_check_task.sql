-- 盘点任务
CREATE TABLE IF NOT EXISTS stock_check_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  task_no VARCHAR(32) NOT NULL COMMENT '任务号，如PD000001',
  task_date DATE NOT NULL COMMENT '盘点日期',
  status TINYINT NOT NULL COMMENT '状态：1-盘点中 2-已盘点 3-已调整',
  finish_time TIMESTAMP NULL COMMENT '结束盘点时间',
  profit_order_id BIGINT NULL COMMENT '盘盈对应入库单ID',
  profit_order_no VARCHAR(32) NULL COMMENT '盘盈对应入库单号',
  loss_order_id BIGINT NULL COMMENT '盘亏对应出库单ID',
  loss_order_no VARCHAR(32) NULL COMMENT '盘亏对应出库单号',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='盘点任务';

CREATE UNIQUE INDEX uk_stock_check_task_no ON stock_check_task(task_no);
CREATE INDEX idx_stock_check_task_status_date ON stock_check_task(status, task_date);
CREATE INDEX idx_stock_check_task_profit_order ON stock_check_task(profit_order_id);
CREATE INDEX idx_stock_check_task_loss_order ON stock_check_task(loss_order_id);
