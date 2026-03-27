-- 出入库申请明细增加货位字段
ALTER TABLE io_apply_detail
  ADD COLUMN location_id BIGINT NULL COMMENT '货位ID' AFTER qty;

CREATE INDEX idx_io_apply_detail_location ON io_apply_detail(location_id);
