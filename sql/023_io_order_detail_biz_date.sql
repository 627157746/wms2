-- io_order_detail 增加业务日期冗余字段
ALTER TABLE io_order_detail
    ADD COLUMN biz_date DATE NULL COMMENT '业务日期' AFTER order_type;

UPDATE io_order_detail d
    JOIN io_order o ON o.id = d.order_id
SET d.biz_date = o.biz_date
WHERE d.biz_date IS NULL;

ALTER TABLE io_order_detail
    MODIFY COLUMN biz_date DATE NOT NULL COMMENT '业务日期';

CREATE INDEX idx_io_order_detail_product_biz_date ON io_order_detail(product_id, biz_date);
