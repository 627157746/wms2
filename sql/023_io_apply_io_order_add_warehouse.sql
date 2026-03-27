-- 出入库申请、出入库单增加仓库字段
ALTER TABLE io_apply
  ADD COLUMN warehouse_id BIGINT NULL COMMENT '仓库ID，仅入库使用' AFTER customer_id;

ALTER TABLE io_order
  ADD COLUMN warehouse_id BIGINT NULL COMMENT '仓库ID，仅入库使用' AFTER customer_id;

CREATE INDEX idx_io_apply_warehouse ON io_apply(order_type, warehouse_id);
CREATE INDEX idx_io_order_warehouse ON io_order(order_type, warehouse_id);
