-- 盘点任务明细
CREATE TABLE IF NOT EXISTS stock_check_task_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  task_id BIGINT NOT NULL COMMENT '盘点任务ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  snapshot_qty BIGINT NOT NULL DEFAULT 0 COMMENT '加入任务时的账面数量',
  actual_qty BIGINT NULL COMMENT '盘点数量',
  diff_qty BIGINT NULL COMMENT '差异数量，盘点数量减账面数量',
  result_type TINYINT NOT NULL DEFAULT 0 COMMENT '盘点结果：0-未盘 1-无差异 2-盘盈 3-盘亏',
  count_time TIMESTAMP NULL COMMENT '录入盘点数量时间',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='盘点任务明细';

CREATE UNIQUE INDEX uk_stock_check_task_detail_task_product ON stock_check_task_detail(task_id, product_id);
CREATE INDEX idx_stock_check_task_detail_task ON stock_check_task_detail(task_id);
CREATE INDEX idx_stock_check_task_detail_product ON stock_check_task_detail(product_id);
