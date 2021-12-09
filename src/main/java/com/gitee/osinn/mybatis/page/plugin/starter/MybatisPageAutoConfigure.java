package com.gitee.osinn.mybatis.page.plugin.starter;

import com.gitee.osinn.mybatis.page.plugin.PagePluginInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis分页插件自动配置
 *
 * @author wency_cai
 */
@Configuration
@EnableConfigurationProperties(MybatisPageProperties.class)
@ConditionalOnProperty(value = MybatisPageProperties.PREFIX + ".enable", havingValue = "true")
public class MybatisPageAutoConfigure {

    @Bean
    public PagePluginInterceptor pagePluginInterceptor() {
        return new PagePluginInterceptor();
    }

}
