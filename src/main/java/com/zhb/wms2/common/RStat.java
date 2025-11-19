package com.zhb.wms2.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author zhb
 * @Description
 * @Date 2025/8/5 11:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RStat<T> {

    private T stat;
    private IPage<T> data;

    public static <T> RStat<T> create(T t) {
//        return new RStat<>(t, Collections.emptyList());
        return new RStat<>(t, null);
    }


}
