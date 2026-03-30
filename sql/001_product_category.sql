-- 商品分类
CREATE TABLE IF NOT EXISTS product_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  name VARCHAR(100) NOT NULL COMMENT '分类名称',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父级ID，0为顶级',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  level INT NOT NULL DEFAULT 1 COMMENT '层级，由系统计算',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  update_by VARCHAR(64) NULL COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类';

CREATE UNIQUE INDEX uk_product_category_name ON product_category(name);
CREATE INDEX idx_product_category_parent ON product_category(parent_id);
CREATE INDEX idx_product_category_sort ON product_category(sort_order);
