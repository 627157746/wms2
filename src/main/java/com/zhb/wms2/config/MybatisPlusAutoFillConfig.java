package com.zhb.wms2.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充配置
 *
 * @author zhb
 * @since 2026/3/26
 */
@Configuration
public class MybatisPlusAutoFillConfig implements MetaObjectHandler {

    /**
     * 无登录态时使用的默认操作人标识。
     */
    private static final String SYSTEM_OPERATOR = "system";

    /**
     * 新增数据时自动填充创建人和更新时间等字段。
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        String operator = getCurrentOperator();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createBy", String.class, operator);
        this.strictInsertFill(metaObject, "updateBy", String.class, operator);
    }

    /**
     * 更新数据时自动填充更新人和更新时间。
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentOperator());
    }

    /**
     * 获取当前操作人，未登录时回退为系统账号。
     */
    private String getCurrentOperator() {
        if (SaHolder.getContext().isValid()) {
            try {
                if (StpUtil.isLogin()) {
                    return StpUtil.getLoginIdAsString();
                }
            } catch (Exception ignored) {
                // 忽略登录态获取异常，回退为系统账号
            }
        }
        return SYSTEM_OPERATOR;
    }
}
