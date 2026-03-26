package com.zhb.wms2.common.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RStat 模型
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RStat<T> {

    /**
     * 统计结果。
     */
    private T stat;

    /**
     * 分页数据。
     */
    private IPage<T> data;

    /**
     * 创建只包含统计对象的返回结构。
     */
    public static <T> RStat<T> create(T t) {
        return new RStat<>(t, null);
    }


}
