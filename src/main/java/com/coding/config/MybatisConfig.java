package com.coding.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.mapper.autoconfigure.ConfigurationCustomizer;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * 注册枚举
 *
 * @author guanweiming
 */
@Slf4j
@EntityScan("com.coding.entity")
@Configuration
@MapperScan("com.coding.mapper")
public class MybatisConfig implements ConfigurationCustomizer {


    @Override
    public void customize(org.apache.ibatis.session.Configuration configuration) {
        TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
    }
}
