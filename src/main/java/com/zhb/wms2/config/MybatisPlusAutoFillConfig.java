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
 * @since 1.0
 */
@Configuration
public class MybatisPlusAutoFillConfig implements MetaObjectHandler {

    private static final String SYSTEM_OPERATOR = "system";

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        String operator = getCurrentOperator();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createBy", String.class, operator);
        this.strictInsertFill(metaObject, "updateBy", String.class, operator);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentOperator());
    }

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
