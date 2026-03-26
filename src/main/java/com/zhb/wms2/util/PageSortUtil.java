package com.zhb.wms2.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhb.wms2.common.model.BaseQuery;
import com.zhb.wms2.common.model.BaseSortQuery;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 逻辑分页与汇总工具。
 *
 * @author zhb
 * @since 2026/3/26
 */
public class PageSortUtil {

    /**
     * 对列表进行逻辑分页。
     */
    public static <T> IPage<T> page(List<T> list, BaseQuery baseQuery) {
        List<T> pageList = ListUtil.page(baseQuery.getCurrent().intValue() - 1, baseQuery.getSize().intValue(), list);
        IPage<T> page = new Page<>(baseQuery.getCurrent(), baseQuery.getSize(), list.size());
        page.setRecords(pageList);
        return page;
    }

    /**
     * 对列表排序后再做逻辑分页。
     */
    public static <T> IPage<T> pageSort(List<T> list, BaseSortQuery baseSortQuery) {
        ListUtil.sortByProperty(list, baseSortQuery.getSortField());
        if (baseSortQuery.getSort().equals("desc")) {
            ListUtil.reverse(list);
        }
        return page(list, baseSortQuery);
    }

    /**
     * 汇总列表中的数值字段。
     */
    public static <T> void stat(T stat, List<T> list, String... ignoreFields) {
        Class<?> clazz = stat.getClass();
        Field[] fields = ReflectUtil.getFields(clazz);
        for (Field field : fields) {
            Class<?> type = field.getType();
            String name = field.getName();
            boolean contains = ArrayUtil.contains(ignoreFields, name);
            if (contains) {
                continue;
            }
            Object value;
            if (type == Integer.class || type == int.class) {
                value = list.stream()
                        .mapToInt(item -> {
                            Integer fieldValue = (Integer) ReflectUtil.getFieldValue(item, name);
                            if (fieldValue == null) {
                                return 0;
                            }
                            return fieldValue;
                        })
                        .sum();
            } else if (type == Long.class || type == long.class) {
                value = list.stream()
                        .mapToLong(item -> {
                            Long fieldValue = (Long) ReflectUtil.getFieldValue(item, name);
                            if (fieldValue == null) {
                                return 0;
                            }
                            return fieldValue;
                        })
                        .sum();
            } else if (type == Double.class || type == double.class) {
                value = list.stream()
                        .mapToDouble(item -> {
                            Double fieldValue = (Double) ReflectUtil.getFieldValue(item, name);
                            if (fieldValue == null) {
                                return 0;
                            }
                            return fieldValue;
                        })
                        .sum();
            } else if (type == BigDecimal.class) {
                value = list.stream()
                        .map(item -> {
                            BigDecimal fieldValue = (BigDecimal) ReflectUtil.getFieldValue(item, name);
                            return Objects.requireNonNullElse(fieldValue, BigDecimal.ZERO);
                        })
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);
            } else {
                continue;
            }
            ReflectUtil.setFieldValue(stat, name, value);
        }
    }
}
