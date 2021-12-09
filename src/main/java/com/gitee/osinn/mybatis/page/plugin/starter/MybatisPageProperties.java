package com.gitee.osinn.mybatis.page.plugin.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mybatis分页插件参数配置
 *
 * @author wency_cai
 */
@ConfigurationProperties(prefix = MybatisPageProperties.PREFIX)
public class MybatisPageProperties {

    public final static String PREFIX = "mybatis.page.config";

    /**
     * 是否开启多租户配置
     */
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
