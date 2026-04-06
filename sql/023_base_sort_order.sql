-- 基础资料排序字段补齐及历史数据回填
ALTER TABLE deliveryman
    ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0 COMMENT '排序' AFTER scope;

ALTER TABLE io_type
    ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0 COMMENT '排序' AFTER scope;

ALTER TABLE salesman
    ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0 COMMENT '排序' AFTER address;

UPDATE deliveryman
SET sort_order = id
WHERE sort_order = 0 OR sort_order IS NULL;

UPDATE io_type
SET sort_order = id
WHERE sort_order = 0 OR sort_order IS NULL;

UPDATE salesman
SET sort_order = id
WHERE sort_order = 0 OR sort_order IS NULL;

UPDATE product_location
SET sort_order = id
WHERE sort_order = 0 OR sort_order IS NULL;

UPDATE product_unit
SET sort_order = id
WHERE sort_order = 0 OR sort_order IS NULL;
