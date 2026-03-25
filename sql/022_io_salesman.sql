-- 出库申请、出库单增加业务员
ALTER TABLE io_apply
  ADD COLUMN IF NOT EXISTS salesman_id BIGINT NULL COMMENT '业务员ID，仅出库使用' AFTER customer_id;

ALTER TABLE io_order
  ADD COLUMN IF NOT EXISTS salesman_id BIGINT NULL COMMENT '业务员ID，仅出库使用' AFTER customer_id;
