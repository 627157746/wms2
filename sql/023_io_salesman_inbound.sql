-- 入库申请、入库单支持业务员字段，并统一字段注释
SET @sql = (
  SELECT IF(
    EXISTS(
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'io_apply'
        AND COLUMN_NAME = 'salesman_id'
    ),
    'ALTER TABLE io_apply MODIFY COLUMN salesman_id BIGINT NULL COMMENT ''业务员ID''',
    'ALTER TABLE io_apply ADD COLUMN salesman_id BIGINT NULL COMMENT ''业务员ID'' AFTER customer_id'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS(
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'io_order'
        AND COLUMN_NAME = 'salesman_id'
    ),
    'ALTER TABLE io_order MODIFY COLUMN salesman_id BIGINT NULL COMMENT ''业务员ID''',
    'ALTER TABLE io_order ADD COLUMN salesman_id BIGINT NULL COMMENT ''业务员ID'' AFTER customer_id'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
